package com.hihihihi.gureumpage.ui.mindmap

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.gyso.treeview.TreeViewEditor
import com.gyso.treeview.adapter.DrawInfo
import com.gyso.treeview.adapter.TreeViewAdapter
import com.gyso.treeview.adapter.TreeViewHolder
import com.gyso.treeview.line.BaseLine
import com.gyso.treeview.model.NodeModel
import com.hihihihi.gureumpage.R
import java.util.UUID

typealias TreeNode = NodeModel<MindMapNodeData>

class MindMapAdapter(
    private val onNodeClick: (TreeNode) -> Unit
) : TreeViewAdapter<MindMapNodeData>() {
    private lateinit var editor: TreeViewEditor // 마인드 맵 편집 객체
    private var selected: TreeNode? = null      // 현재 선택된 노드

    fun setEditor(treeViewEditor: TreeViewEditor) {
        editor = treeViewEditor
        editor.requestMoveNodeByDragging(false) // 화면 드래그 활성화
    }

    override fun onCreateViewHolder(parent: ViewGroup, node: TreeNode) =
        TreeViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_mindmap_node, parent, false), node
        )

    override fun onBindViewHolder(holder: TreeViewHolder<MindMapNodeData>) {
        holder.view.findViewById<TextView>(R.id.node_text).text = holder.node.value.content
        holder.view.setOnClickListener {
            selected = holder.node
            onNodeClick(holder.node) // 이벤트 처리할 노드 전달
        }
    }

    // TODO: 추후 간선 스타일 지정할 때 사용
    override fun onDrawLine(drawInfo: DrawInfo?): BaseLine? = null

    fun addChildToSelected() {
        selected?.let {
            editor.addChildNodes(
                it,
                TreeNode(MindMapNodeData(UUID.randomUUID().toString(), "새 노드"))
            )
        }
    }

    fun removeSelected() {
        selected?.let { editor.removeNode(it) }
    }

    fun focusCenter() {
        editor.focusMidLocation()
    }
}