package com.hihihihi.gureumpage.ui.mindmap

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hihihihi.domain.model.MindmapNode
import com.hihihihi.domain.operation.NodeEditOperation
import com.hihihihi.domain.usecase.mindmap.GetMindmapUseCase
import com.hihihihi.domain.usecase.mindmapnode.ApplyNodeOperation
import com.hihihihi.domain.usecase.mindmapnode.ObserveUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MindMapViewModel @Inject constructor(
    private val observeUseCase: ObserveUseCase,
    private val applyNodeOperation: ApplyNodeOperation,
    private val getMindmapUseCase: GetMindmapUseCase
) : ViewModel() {
    private val _nodes = MutableStateFlow<List<MindmapNode>>(emptyList())
    val nodes: StateFlow<List<MindmapNode>> = _nodes

    private val _pending = MutableStateFlow<List<NodeEditOperation>>(emptyList())

    private var saving by mutableStateOf(false)
    private var autosaveJob: Job? = null

    var editing by mutableStateOf(false)
        private set
    lateinit var mindmapId: String
        private set

    fun load(mindmapId: String) {
        this.mindmapId = mindmapId
        viewModelScope.launch {
            observeUseCase(mindmapId).collect { if (!editing && !saving) _nodes.value = it }
        }
    }

    fun startEdit() {
        editing = true
        _pending.value = emptyList()
    }

    fun endEdit(autoSave: Boolean = true) {
        if (!editing) return
        viewModelScope.launch {
            if (autoSave) flush()                          // 남은 델타 저장
            editing = false
        }
    }

    fun record(operation: NodeEditOperation) {
        _pending.update { reduce(it + operation) }
        scheduleAutosave()
    }

    fun saveAndExit() {
        viewModelScope.launch {
            flush()
            editing = false
        }
//        val operations = _pending.value
//        viewModelScope.launch {
//            if (operations.isNotEmpty()) applyNodeOperation(mindmapId, operations)
//            _pending.value = emptyList()
//            editing = false
//        }
    }

    private fun scheduleAutosave() {
        autosaveJob?.cancel()
        autosaveJob = viewModelScope.launch {
            delay(800)                                     // 0.8s 디바운스
            flush()
        }
    }

    private suspend fun flush() {
        val ops = _pending.value
        if (ops.isEmpty()) return
        saving = true
        try {
            applyNodeOperation(mindmapId, ops).getOrThrow()
            _pending.value = emptyList()
        } catch (t: Throwable) {
            // 실패 시 펜딩 유지(재시도 가능)
        } finally {
            saving = false
        }
    }

    // 같은 노드에 대한 연속 작업을 최소화
    private fun reduce(ops: List<NodeEditOperation>): List<NodeEditOperation> {
        val adds = LinkedHashMap<String, MindmapNode>()
        val updates = LinkedHashMap<String, MindmapNode>()
        val deletes = LinkedHashSet<String>()

        ops.forEach { operation ->
            when (operation) {
                is NodeEditOperation.Add -> {
                    deletes.remove(operation.node.mindmapNodeId)
                    val id = operation.node.mindmapNodeId
                    adds[id] = (adds[id]?.copy(
                        nodeTitle = operation.node.nodeTitle,
                        nodeEx = operation.node.nodeEx,
                        icon = operation.node.icon,
                        color = operation.node.color,
                        parentNodeId = operation.node.parentNodeId
                    )) ?: operation.node
                    updates.remove(id)
                }

                is NodeEditOperation.Update -> {
                    val id = operation.node.mindmapNodeId
                    if (adds.containsKey(id)) {
                        adds[id] = adds[id]!!.copy(
                            nodeTitle = operation.node.nodeTitle,
                            nodeEx = operation.node.nodeEx,
                            icon = operation.node.icon,
                            color = operation.node.color,
                            parentNodeId = operation.node.parentNodeId
                        )
                    } else if (!deletes.contains(id)) {
                        updates[id] = operation.node
                    }
                }

                is NodeEditOperation.Delete -> {
                    adds.remove(operation.nodeId)
                    updates.remove(operation.nodeId)
                    deletes.add(operation.nodeId)
                }
            }
        }

        return buildList {
            adds.values.forEach {
                add(NodeEditOperation.Add(it))
            }
            updates.values.forEach {
                add(NodeEditOperation.Update(it))
            }
            deletes.forEach {
                add(NodeEditOperation.Delete(it))
            }
        }
    }
}