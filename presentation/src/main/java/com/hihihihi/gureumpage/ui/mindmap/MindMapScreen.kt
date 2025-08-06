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
fun MindMapScreen(/*책 id 받기*/) {
    val context = LocalContext.current
    val adapter = remember { MindMapAdapter { } }
    var initialized by remember { mutableStateOf(false) }   // init 여부
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
                    50, // 부모 - 자식 간 거리
                    20, // 노드 간 거리
                    DashLine(lineColor, 8)
                )
            )
            adapter.setEditor(baseTreeView.editor) // 에디터 탑재

            val root = NodeModel(MindMapNodeData(UUID.randomUUID().toString(), "책 제목"))
            val model = TreeModel(root).apply {
                addNode(root, NodeModel(MindMapNodeData(UUID.randomUUID().toString(), "아이디어 1")))
                addNode(root, NodeModel(MindMapNodeData(UUID.randomUUID().toString(), "아이디어 2")))
            }
            adapter.setTreeModel(model)

            initialized = true
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
private fun test() {
    GureumPageTheme {
        MindMapScreen()
    }
}