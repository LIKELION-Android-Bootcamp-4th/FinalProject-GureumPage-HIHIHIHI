package com.hihihihi.gureumpage.ui.mindmap

import android.view.View
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import com.gyso.treeview.layout.VerticalTreeLayoutManager
import com.gyso.treeview.line.DashLine
import com.gyso.treeview.model.NodeModel
import com.gyso.treeview.model.TreeModel
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.hilt.navigation.compose.hiltViewModel
import com.hihihihi.domain.model.MindmapNode
import com.hihihihi.gureumpage.databinding.ActivityMindmapBinding
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.ui.mindmap.mapper.toUi
import kotlin.collections.sortedBy

@Composable
fun MindMapScreen(
    bookId: String,
    mindmapId: String,
    viewModel: MindMapViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val adapter = remember { MindMapAdapter(context) }
    val nodes by viewModel.nodes.collectAsState() // 스냅샷
    val lineColor = GureumTheme.colors.gray200.toArgb()

    LaunchedEffect(mindmapId) {
        viewModel.load(mindmapId)
    }

    DisposableEffect(Unit) {
        onDispose {
            // 편집 중일때 나가도 자동 저장
            if (viewModel.editing) {
                viewModel.endEdit(adapter.asDomainList(mindmapId), autoSave = true)
            }
        }
    }

    AndroidViewBinding(
        factory = ActivityMindmapBinding::inflate,
        modifier = Modifier.fillMaxSize()
    ) {
        if (baseTreeView.adapter !is MindMapAdapter) {
            baseTreeView.adapter = adapter
            baseTreeView.setTreeLayoutManager(
                VerticalTreeLayoutManager(
                    context,
                    50,     // 부모 - 자식 간 거리
                    20,       // 노드 간 거리
                    DashLine(lineColor, 8)
                )
            )
            adapter.setEditor(baseTreeView.editor) // 에디터 탑재
        }
        adapter.mindmapId = mindmapId

        // 편집 중이 아닐 때만 원격 스냅샷으로 트리 구성
        if (!viewModel.editing && nodes.isNotEmpty() && !adapter.sameAs(nodes, mindmapId)) {
            val root = nodes.firstOrNull { it.parentNodeId == null } ?: return@AndroidViewBinding
            val modelRoot = NodeModel(root.toUi())
            val model = TreeModel(modelRoot)
            val map = mutableMapOf(root.mindmapNodeId to modelRoot)

            fun attach(pid: String) {
                nodes.filter { it.parentNodeId == pid }.forEach { child ->
                    val cm = NodeModel(child.toUi())
                    model.addNode(map[pid]!!, cm)
                    map[child.mindmapNodeId] = cm
                    attach(child.mindmapNodeId)
                }
            }
            attach(root.mindmapNodeId)
            adapter.setTreeModel(model)
        }

        // 편집 모드일 때 수정, 삭제 버튼 이벤트 처리
        adapter.onNodeAction = { node, action ->
            when (action) {
                MindMapAdapter.NodeAction.EDIT -> {
                    adapter.showAddNodeDialog(rootFrame.context, node) { newNode ->

                        node.value = newNode
                        adapter.notifyDataSetChange()
                    }
                }

                MindMapAdapter.NodeAction.DELETE -> {
                    adapter.performDelete(node)
                }
            }
        }
        btnCenter.setOnClickListener { adapter.focusCenter() }
        btnUndo.setOnClickListener { adapter.undo() }
        btnRedo.setOnClickListener { adapter.redo() }

        switchEditMode.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) viewModel.startEdit() else viewModel.endEdit(
                adapter.asDomainList(mindmapId),
                autoSave = true
            )
            adapter.changeEditMode(isChecked)
            btnRedo.visibility = if (isChecked) View.VISIBLE else View.INVISIBLE
            btnUndo.visibility = if (isChecked) View.VISIBLE else View.INVISIBLE
        }
    }
}

// 어댑터와 원격의 트리 비교
private fun MindMapAdapter.sameAs(nodes: List<MindmapNode>, mindmapId: String): Boolean {
    val cur = asDomainList(mindmapId).sortedBy { it.mindmapNodeId }
    val src = nodes.sortedBy { it.mindmapNodeId }
    return cur == src
}