package com.hihihihi.gureumpage.ui.mindmap

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hihihihi.domain.model.MindmapNode
import com.hihihihi.domain.operation.NodeEditOperation
import com.hihihihi.domain.usecase.mindmapnode.ApplyNodeOperation
import com.hihihihi.domain.usecase.mindmapnode.ObserveUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MindMapViewModel @Inject constructor(
    private val observeUseCase: ObserveUseCase,             // 노드 실시간 스트림
    private val applyNodeOperation: ApplyNodeOperation,     // 일괄 변경 적용
) : ViewModel() {
    // 화면에 그릴 스냅샷 노드들
    private val _nodes = MutableStateFlow<List<MindmapNode>>(emptyList())
    val nodes: StateFlow<List<MindmapNode>> = _nodes

    // 편집 시작 시점 스냅샷
    private var baseline: List<MindmapNode> = emptyList()

    // observe 수신, 저장이 충돌하지 않게하는 플래그
    private var saving by mutableStateOf(false)

    var editing by mutableStateOf(false)
        private set
    lateinit var mindmapId: String
        private set

    // 마인드맵 로드
    fun load(mindmapId: String) {
        this.mindmapId = mindmapId
        viewModelScope.launch {
            // 편집중이거나 수정중이 아닐 떄 덮어씀
            observeUseCase(mindmapId).collect { if (!editing && !saving) _nodes.value = it }
        }
    }

    // 편집 시작. 현재 화면 상태를 baseLine으로 고정
    fun startEdit() {
        editing = true
        baseline = _nodes.value
    }

    // 편집 종료. 로컬과 서버 연동
    fun endEdit(currentTree: List<MindmapNode>, autoSave: Boolean = true) {
        if (!editing) return
        viewModelScope.launch {
            if (autoSave) {
                flushDiff(currentTree)
                _nodes.value = currentTree
            }
            editing = false
        }
    }

    // baseLine과 현재 노드 차이 계산해 서버에 적용
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

    // 두 스냅샷 차이를 계산
    private fun diff(oldList: List<MindmapNode>, newList: List<MindmapNode>): List<NodeEditOperation> {
        val old = oldList.associateBy { it.mindmapNodeId }
        val neu = newList.associateBy { it.mindmapNodeId }
        val operations = mutableListOf<NodeEditOperation>()

        // 삭제
        for ((id, _) in old) if (id !in neu) operations += NodeEditOperation.Delete(id)

        // 추가/수정
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