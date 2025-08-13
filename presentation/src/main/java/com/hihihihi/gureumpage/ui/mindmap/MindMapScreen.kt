package com.hihihihi.gureumpage.ui.mindmap

import android.view.View
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.gyso.treeview.layout.VerticalTreeLayoutManager
import com.gyso.treeview.line.DashLine
import com.gyso.treeview.model.NodeModel
import com.gyso.treeview.model.TreeModel
import java.util.UUID
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.hilt.navigation.compose.hiltViewModel
import com.hihihihi.gureumpage.databinding.ActivityMindmapBinding
import com.hihihihi.gureumpage.designsystem.theme.GureumPageTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.ui.mindmap.mapper.toUi
import com.hihihihi.gureumpage.ui.mindmap.model.MindMapNodeData
import kotlinx.coroutines.launch

@Composable
fun MindMapScreen(
    bookId: String,
    mindmapId: String,
    viewModel: MindMapViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val adapter = remember { MindMapAdapter(context) }
    val scope = rememberCoroutineScope()
    val nodes by viewModel.nodes.collectAsState()
    val lineColor = GureumTheme.colors.gray200.toArgb()

    LaunchedEffect(mindmapId) {
        viewModel.load(mindmapId)
    }

    DisposableEffect(Unit) {
        adapter.onEditOperation = { op -> if (viewModel.editing) viewModel.record(op) }
        onDispose {
            scope.launch { viewModel.endEdit(autoSave = true) }
            adapter.onEditOperation = null
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

            val root =
                NodeModel(MindMapNodeData(UUID.randomUUID().toString(), "책 제목", "", null)) // TODO: 책 이름과 책 이미지 연결하기
            val model = TreeModel(root).apply {
                addNode(
                    root,
                    NodeModel(MindMapNodeData(UUID.randomUUID().toString(), "클릭해서 노드를 추가해요", ""))
                ) // TODO: 처음 만들 때 기본 노드 개수 상의
                addNode(root, NodeModel(MindMapNodeData(UUID.randomUUID().toString(), "꾹 눌러서 수정하거나 삭제해요", "")))
            }
            adapter.setTreeModel(model)
        }
        adapter.mindmapId = mindmapId

        // 편집 중이 아닐 때만 원격 스냅샷으로 트리 구성
        if (!viewModel.editing && nodes.isNotEmpty()) {
            val root = nodes.firstOrNull { it.parentNodeId == null }
            if (root != null) {
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
            adapter.changeEditMode(isChecked)
            if (isChecked) viewModel.startEdit() else viewModel.endEdit(autoSave = true)
            btnRedo.visibility = if (isChecked) View.VISIBLE else View.INVISIBLE
            btnUndo.visibility = if (isChecked) View.VISIBLE else View.INVISIBLE
        }
    }
}

@Preview
@Composable
private fun MindMapPreview() {
    GureumPageTheme {
//        MindMapScreen("",)/
    }
}