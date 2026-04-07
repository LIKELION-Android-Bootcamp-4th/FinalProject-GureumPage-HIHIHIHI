package com.hihihihi.presentation.ui.mindmap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hihihihi.domain.model.MindmapNode
import com.hihihihi.domain.operation.NodeEditOperation
import com.hihihihi.domain.usecase.mindmapnode.ApplyNodeOperation
import com.hihihihi.domain.usecase.mindmapnode.ObserveMindmapNodeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MindMapUiState(
    val nodes: List<MindmapNode> = emptyList(),
    val editing: Boolean = false,
)

@HiltViewModel
class MindMapViewModel @Inject constructor(
    private val observeMindmapNodeUseCase: ObserveMindmapNodeUseCase,
    private val applyNodeOperation: ApplyNodeOperation,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MindMapUiState())
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
            observeMindmapNodeUseCase(mindmapId).collect { nodes ->
                if (!_uiState.value.editing && !saving) {
                    _uiState.update { it.copy(nodes = nodes) }
                }
            }
        }
    }

    fun startEdit() {
        baseline = _uiState.value.nodes
        _uiState.update { it.copy(editing = true) }
    }

    fun endEdit(currentTree: List<MindmapNode>, autoSave: Boolean = true) {
        if (!_uiState.value.editing) return
        viewModelScope.launch {
            if (autoSave) {
                flushDiff(currentTree)
                _uiState.update { it.copy(nodes = currentTree) }
            }
            _uiState.update { it.copy(editing = false) }
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
}
