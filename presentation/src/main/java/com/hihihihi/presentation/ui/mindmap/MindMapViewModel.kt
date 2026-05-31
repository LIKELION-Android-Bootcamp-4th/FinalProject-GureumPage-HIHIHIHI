package com.hihihihi.presentation.ui.mindmap

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.hanhyo.composemindmap.controller.MindMapEditController
import io.github.hanhyo.composemindmap.model.MindMapNode
import io.github.hanhyo.composemindmap.model.MindMapNodeWithPayload
import com.hihihihi.domain.model.MindmapNode
import com.hihihihi.domain.operation.NodeEditOperation
import com.hihihihi.domain.usecase.mindmapnode.ApplyNodeOperation
import com.hihihihi.domain.usecase.mindmapnode.ObserveMindmapNodeUseCase
import com.hihihihi.presentation.ui.mindmap.mapper.toDomain
import com.hihihihi.presentation.ui.mindmap.mapper.toLibraryModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@Immutable
sealed interface MindMapUiState {
    @Immutable
    data object Loading : MindMapUiState

    @Immutable
    data class Content(
        val nodes: List<MindMapNodeWithPayload<GureumMindMapPayload>> = emptyList(),
        val editMode: Boolean = false,
        val selectedNodeId: String? = null,
        val canUndo: Boolean = false,
        val canRedo: Boolean = false,
    ) : MindMapUiState

    @Immutable
    data class Error(
        val message: String,
        val previous: Content? = null,
    ) : MindMapUiState
}

sealed interface MindMapEvent {
    data class NodeTapped(val nodeId: String) : MindMapEvent
    data class NodeLongPressed(val nodeId: String) : MindMapEvent
    data object CanvasTapped : MindMapEvent
    data object StartEdit : MindMapEvent
    data object EndEdit : MindMapEvent
    data object SaveAndNavigateBack : MindMapEvent
    data class AddNode(
        val parentId: String?,
        val title: String,
        val subtitle: String,
        val icon: String?,
        val color: Color?,
    ) : MindMapEvent
    data class UpdateNode(
        val nodeId: String,
        val title: String,
        val subtitle: String,
        val icon: String?,
        val color: Color?,
    ) : MindMapEvent
    data class DeleteNode(val nodeId: String) : MindMapEvent
    data class MoveNode(val nodeId: String, val newParentId: String) : MindMapEvent
    data object Undo : MindMapEvent
    data object Redo : MindMapEvent
    data class ShowAddChildSheet(val parentId: String) : MindMapEvent
}

sealed interface MindMapEffect {
    data class ShowToast(val message: String) : MindMapEffect
    data class ShowNodeEditSheet(val node: MindMapNode?, val parentId: String? = null) : MindMapEffect
    data class ShowDeleteConfirm(val nodeId: String) : MindMapEffect
    data class ShowNodeDetail(val node: MindMapNode) : MindMapEffect
    data object NavigateBack : MindMapEffect
}

@HiltViewModel
class MindMapViewModel @Inject constructor(
    private val observeMindmapNodeUseCase: ObserveMindmapNodeUseCase,
    private val applyNodeOperation: ApplyNodeOperation,
) : ViewModel() {

    private val _uiState = MutableStateFlow<MindMapUiState>(MindMapUiState.Loading)
    val uiState: StateFlow<MindMapUiState> = _uiState.asStateFlow()

    private val _effect = Channel<MindMapEffect>(Channel.BUFFERED)
    val effect: Flow<MindMapEffect> = _effect.receiveAsFlow()

    private val controller = MindMapEditController()

    private var mindmapId: String = ""
    private var userId: String = ""
    private var baselineDomain: List<MindmapNode> = emptyList()
    private var currentDomainNodes: List<MindmapNode> = emptyList()
    private var payloadByNodeId: Map<String, GureumMindMapPayload> = emptyMap()
    private var saving = false

    fun load(mindmapId: String) {
        this.mindmapId = mindmapId
        viewModelScope.launch {
            observeMindmapNodeUseCase(mindmapId).collect { nodes ->
                if (userId.isEmpty() && nodes.isNotEmpty()) userId = nodes.first().userId
                payloadByNodeId = nodes.associate { it.mindmapNodeId to GureumMindMapPayload(it.bookImage) }
                val editMode = (_uiState.value as? MindMapUiState.Content)?.editMode ?: false
                if (!editMode && !saving) {
                    currentDomainNodes = nodes
                    updateContent { it.copy(nodes = nodes.map { n -> n.toLibraryModel() }) }
                }
            }
        }
    }

    fun onEvent(event: MindMapEvent) {
        when (event) {
            MindMapEvent.StartEdit -> {
                baselineDomain = currentDomainNodes
                controller.reset()
                updateContent { it.copy(editMode = true, canUndo = false, canRedo = false) }
            }
            MindMapEvent.EndEdit -> finishEditing()
            MindMapEvent.SaveAndNavigateBack -> finishEditing {
                _effect.send(MindMapEffect.NavigateBack)
            }
            is MindMapEvent.NodeTapped -> {
                val content = _uiState.value as? MindMapUiState.Content ?: return
                updateContent { it.copy(selectedNodeId = event.nodeId) }
                if (!content.editMode) {
                    val node = content.nodes.firstOrNull { it.node.id == event.nodeId }?.node
                    if (node != null) {
                        viewModelScope.launch { _effect.send(MindMapEffect.ShowNodeDetail(node)) }
                    }
                }
            }
            MindMapEvent.CanvasTapped -> updateContent { it.copy(selectedNodeId = null) }
            is MindMapEvent.NodeLongPressed -> {
                val content = _uiState.value as? MindMapUiState.Content ?: return
                if (content.editMode) {
                    val node = content.nodes.firstOrNull { it.node.id == event.nodeId }?.node ?: return
                    if (!canDeleteMindMapNode(node)) {
                        showRootNodeDeletionBlockedToast()
                        return
                    }
                    viewModelScope.launch { _effect.send(MindMapEffect.ShowDeleteConfirm(event.nodeId)) }
                }
            }
            is MindMapEvent.AddNode -> {
                val newNode = MindMapNode(
                    id = UUID.randomUUID().toString(),
                    title = event.title,
                    subtitle = event.subtitle,
                    parentId = event.parentId,
                    icon = event.icon,
                    color = event.color,
                )
                val content = _uiState.value as? MindMapUiState.Content ?: return
                commitNodes(controller.addNode(content.plainNodes, newNode))
            }
            is MindMapEvent.UpdateNode -> {
                val content = _uiState.value as? MindMapUiState.Content ?: return
                val before = content.plainNodes.firstOrNull { it.id == event.nodeId } ?: return
                val after = before.copy(
                    title = event.title,
                    subtitle = event.subtitle,
                    icon = event.icon,
                    color = event.color,
                )
                commitNodes(controller.updateNode(content.plainNodes, before, after))
            }
            is MindMapEvent.DeleteNode -> {
                val content = _uiState.value as? MindMapUiState.Content ?: return
                val target = content.plainNodes.firstOrNull { it.id == event.nodeId } ?: return
                if (!canDeleteMindMapNode(target)) {
                    showRootNodeDeletionBlockedToast()
                    return
                }
                val subtree = controller.collectSubtree(content.plainNodes, event.nodeId)
                commitNodes(controller.deleteNode(content.plainNodes, target, subtree))
            }
            MindMapEvent.Undo -> {
                val content = _uiState.value as? MindMapUiState.Content ?: return
                val newNodes = controller.undo(content.plainNodes) ?: return
                commitNodes(newNodes)
            }
            MindMapEvent.Redo -> {
                val content = _uiState.value as? MindMapUiState.Content ?: return
                val newNodes = controller.redo(content.plainNodes) ?: return
                commitNodes(newNodes)
            }
            is MindMapEvent.MoveNode -> {
                val content = _uiState.value as? MindMapUiState.Content ?: return
                if (controller.isCyclic(content.plainNodes, event.nodeId, event.newParentId)) {
                    viewModelScope.launch { _effect.send(MindMapEffect.ShowToast("이동할 수 없습니다")) }
                    return
                }
                val moved = controller.moveNode(content.plainNodes, event.nodeId, event.newParentId)
                if (moved == content.plainNodes) {
                    viewModelScope.launch { _effect.send(MindMapEffect.ShowToast("이동할 수 없습니다")) }
                    return
                }
                commitNodes(moved)
            }
            is MindMapEvent.ShowAddChildSheet -> {
                viewModelScope.launch { _effect.send(MindMapEffect.ShowNodeEditSheet(null, event.parentId)) }
            }
        }
    }

    private val MindMapUiState.Content.plainNodes: List<MindMapNode>
        get() = nodes.map { it.node }

    private fun commitNodes(nodes: List<MindMapNode>) {
        currentDomainNodes = nodes.map { it.toDomain(mindmapId, userId, payloadByNodeId[it.id]?.bookImage) }
        updateContent {
            it.copy(
                nodes = nodes.map { node ->
                    MindMapNodeWithPayload(node, payloadByNodeId[node.id] ?: GureumMindMapPayload(null))
                },
                canUndo = controller.canUndo,
                canRedo = controller.canRedo,
            )
        }
    }

    private fun showRootNodeDeletionBlockedToast() {
        viewModelScope.launch {
            _effect.send(MindMapEffect.ShowToast("루트 노드는 삭제할 수 없습니다"))
        }
    }

    private fun finishEditing(onSaved: suspend () -> Unit = {}) {
        if (saving) return
        viewModelScope.launch {
            if (!flushDiff(currentDomainNodes)) return@launch
            controller.reset()
            updateContent { it.copy(editMode = false, selectedNodeId = null, canUndo = false, canRedo = false) }
            onSaved()
        }
    }

    private suspend fun flushDiff(current: List<MindmapNode>): Boolean {
        val operations = diff(baselineDomain, current)
        if (operations.isEmpty()) return true
        saving = true
        try {
            return applyNodeOperation(mindmapId, operations)
                .onFailure { _effect.send(MindMapEffect.ShowToast("저장에 실패했습니다")) }
                .isSuccess
        } finally {
            saving = false
        }
    }

    private fun diff(oldList: List<MindmapNode>, newList: List<MindmapNode>): List<NodeEditOperation> {
        val old = oldList.associateBy { it.mindmapNodeId }
        val neu = newList.associateBy { it.mindmapNodeId }
        val operations = mutableListOf<NodeEditOperation>()
        for ((id, _) in old) if (id !in neu) operations += NodeEditOperation.Delete(id)
        for ((id, n) in neu) {
            val o = old[id]
            if (o == null) {
                operations += NodeEditOperation.Add(n)
            } else if (
                o.nodeTitle != n.nodeTitle || o.nodeEx != n.nodeEx ||
                o.parentNodeId != n.parentNodeId || o.icon != n.icon ||
                o.color != n.color || o.bookImage != n.bookImage
            ) {
                operations += NodeEditOperation.Update(n)
            }
        }
        return operations
    }

    private fun updateContent(transform: (MindMapUiState.Content) -> MindMapUiState.Content) {
        _uiState.update { current ->
            transform(current as? MindMapUiState.Content ?: MindMapUiState.Content())
        }
    }
}
