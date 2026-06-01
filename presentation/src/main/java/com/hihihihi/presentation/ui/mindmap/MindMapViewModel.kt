package com.hihihihi.presentation.ui.mindmap

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hihihihi.domain.model.MindmapNode
import com.hihihihi.domain.operation.NodeEditOperation
import com.hihihihi.domain.usecase.mindmapnode.ApplyNodeOperation
import com.hihihihi.domain.usecase.mindmapnode.ObserveMindmapNodeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@Immutable
sealed interface MindMapUiState {
    @Immutable
    data object Loading : MindMapUiState

    @Immutable
    data class Content(
        val nodes: List<MindmapNode> = emptyList(),
        val editing: Boolean = false,
    ) : MindMapUiState

    @Immutable
    data class Error(
        val message: String,
        val previous: Content? = null,
    ) : MindMapUiState
}

internal fun MindMapUiState.contentOrDefault(): MindMapUiState.Content = when (this) {
    is MindMapUiState.Content -> this
    is MindMapUiState.Error -> previous ?: MindMapUiState.Content()
    MindMapUiState.Loading -> MindMapUiState.Content()
}

@HiltViewModel
class MindMapViewModel @Inject constructor(
    private val observeMindmapNodeUseCase: ObserveMindmapNodeUseCase,
    private val applyNodeOperation: ApplyNodeOperation,
) : ViewModel() {

    private val _uiState = MutableStateFlow<MindMapUiState>(MindMapUiState.Content())
    val uiState: StateFlow<MindMapUiState> = _uiState.asStateFlow()

    // 편집 시작 시점 스냅샷
    private var baseline: List<MindmapNode> = emptyList()

    // observe 수신과 저장이 충돌하지 않게 하는 플래그
    private var saving = false

    lateinit var mindmapId: String
        private set

    fun load(mindmapId: String) {
        this.mindmapId = mindmapId
        viewModelScope.launch {
            _uiState.value = MindMapUiState.Loading
            try {
                observeMindmapNodeUseCase(mindmapId).collect { nodes ->
                    if (!_uiState.value.contentOrDefault().editing && !saving) {
                        updateContent { it.copy(nodes = nodes) }
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { current ->
                    MindMapUiState.Error(
                        message = e.message ?: "마인드맵을 불러오지 못했습니다.",
                        previous = current as? MindMapUiState.Content,
                    )
                }
            }
        }
    }

    fun startEdit() {
        baseline = _uiState.value.contentOrDefault().nodes
        updateContent { it.copy(editing = true) }
    }

    fun endEdit(currentTree: List<MindmapNode>, autoSave: Boolean = true) {
        if (!_uiState.value.contentOrDefault().editing) return
        viewModelScope.launch {
            try {
                if (autoSave) {
                    flushDiff(currentTree)
                    updateContent { it.copy(nodes = currentTree) }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { current ->
                    MindMapUiState.Error(
                        message = e.message ?: "마인드맵을 저장하지 못했습니다.",
                        previous = current.contentOrDefault().copy(editing = false),
                    )
                }
            } finally {
                updateContent { it.copy(editing = false) }
            }
        }
    }

    private suspend fun flushDiff(current: List<MindmapNode>) {
        val operations = diff(baseline, current)
        if (operations.isEmpty()) return
        saving = true
        try {
            applyNodeOperation(mindmapId, operations).getOrThrow()
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
                o.nodeTitle != n.nodeTitle ||
                o.nodeEx != n.nodeEx ||
                o.parentNodeId != n.parentNodeId ||
                o.icon != n.icon ||
                o.color != n.color ||
                o.bookImage != n.bookImage
            ) {
                operations += NodeEditOperation.Update(n)
            }
        }
        return operations
    }

    private inline fun updateContent(
        crossinline transform: (MindMapUiState.Content) -> MindMapUiState.Content,
    ) {
        _uiState.update { current -> transform(current.contentOrDefault()) }
    }
}
