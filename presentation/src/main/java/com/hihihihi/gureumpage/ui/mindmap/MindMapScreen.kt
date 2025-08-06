package com.hihihihi.gureumpage.ui.mindmap

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.gyso.treeview.layout.VerticalTreeLayoutManager
import com.gyso.treeview.line.DashLine
import com.gyso.treeview.model.NodeModel
import com.gyso.treeview.model.TreeModel
import java.util.UUID
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.core.graphics.toColorInt
import com.gyso.treeview.layout.RightTreeLayoutManager
import com.hihihihi.gureumpage.databinding.ActivityMindmapBinding

@Composable
fun MindMapScreen() {
    val context = LocalContext.current
    val adapter = remember { MindMapAdapter { } }
    var initialized by remember { mutableStateOf(false) }

    AndroidViewBinding(
        factory = ActivityMindmapBinding::inflate,
        modifier = Modifier.fillMaxSize()
    ) {
        if (!initialized) {
            baseTreeView.adapter = adapter
            baseTreeView.setTreeLayoutManager(
                RightTreeLayoutManager(
                    context, 50, 20,
                    DashLine("#4DB6AC".toColorInt(), 8)
                )
            )
            adapter.setEditor(baseTreeView.editor) // 에디터 탑재

            val root = NodeModel(MindMapNodeData(UUID.randomUUID().toString(), "책 제목"))
            val model = TreeModel(root).apply {
                addNode(root, NodeModel(MindMapNodeData(UUID.randomUUID().toString(), "아이디어 1")))
            }
            adapter.setTreeModel(model)

            initialized = true
        }

        btnAdd.setOnClickListener { adapter.addChildToSelected() }
        btnDelete.setOnClickListener { adapter.removeSelected() }
        btnCenter.setOnClickListener { adapter.focusCenter() }
    }
}
