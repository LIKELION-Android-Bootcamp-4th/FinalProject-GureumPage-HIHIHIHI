package com.hihihihi.gureumpage.ui.mindmap

import android.view.View
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.hihihihi.gureumpage.databinding.ActivityMindmapBinding
import com.hihihihi.gureumpage.designsystem.theme.GureumPageTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme

@Composable
fun MindMapScreen(bookId: String) {
    val context = LocalContext.current
    val adapter = remember { MindMapAdapter(context) }
    var initialized by remember { mutableStateOf(false) } // init 여부
    val lineColor = GureumTheme.colors.gray200.toArgb()

    AndroidViewBinding(
        factory = ActivityMindmapBinding::inflate,
        modifier = Modifier.fillMaxSize()
    ) {
        if (!initialized) {
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

            val root = NodeModel(MindMapNodeData(UUID.randomUUID().toString(), "책 제목", "", null)) // TODO: 책 이름과 책 이미지 연결하기
            val model = TreeModel(root).apply {
                addNode(root, NodeModel(MindMapNodeData(UUID.randomUUID().toString(), "아이디어 1", ""))) // TODO: 처음 만들 때 기본 노드 개수 상의
                addNode(root, NodeModel(MindMapNodeData(UUID.randomUUID().toString(), "아이디어 2", "")))
            }
            adapter.setTreeModel(model)

            initialized = true
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
            btnRedo.visibility = if (isChecked) View.VISIBLE else View.INVISIBLE
            btnUndo.visibility = if (isChecked) View.VISIBLE else View.INVISIBLE
        }
    }
}

@Preview
@Composable
private fun MindMapPreview() {
    GureumPageTheme {
        MindMapScreen("")
    }
}