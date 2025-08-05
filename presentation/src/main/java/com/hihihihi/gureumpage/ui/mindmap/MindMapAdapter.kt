package com.hihihihi.gureumpage.ui.mindmap

import android.view.ViewGroup
import com.gyso.treeview.TreeViewEditor
import com.gyso.treeview.adapter.DrawInfo
import com.gyso.treeview.adapter.TreeViewAdapter
import com.gyso.treeview.adapter.TreeViewHolder
import com.gyso.treeview.line.BaseLine
import com.gyso.treeview.model.NodeModel
import java.util.UUID

typealias TreeNode = NodeModel<MindMapNodeData>

class MindMapAdapter(
    private val onNodeClick: (TreeNode) -> Unit     // 노드를 클릭했을 때 행동을 받음
) : TreeViewAdapter<MindMapNodeData>() {
    private var selected: TreeNode? = null          // 현재 선택된 노드
    private lateinit var editor: TreeViewEditor     // 트리 에디터

    override fun onCreateViewHolder(parent: ViewGroup, node: TreeNode): TreeViewHolder<MindMapNodeData> =
        TreeViewHolder(android.widget.TextView(parent.context), node)

    override fun onBindViewHolder(holder: TreeViewHolder<MindMapNodeData>) {
        holder.view.setOnClickListener {
            selected = holder.node
            onNodeClick(holder.node)
        }
    }

    // TODO: 추후 간선 스타일 지정할 때 사용
    override fun onDrawLine(drawInfo: DrawInfo?): BaseLine? = null

    fun addChildToSelected() {
        selected?.let {
            val newNode = TreeNode(MindMapNodeData(UUID.randomUUID().toString(), "새 노드"))
            editor.addChildNodes(it, newNode)
        }
    }

    fun removeSelected() {
        selected?.let { editor.removeNode(it) }
    }

    fun focusCenter() {
        editor.focusMidLocation()
    }
}