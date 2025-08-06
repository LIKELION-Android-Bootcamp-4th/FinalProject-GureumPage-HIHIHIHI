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
    private var editMode: Boolean = false       // 편집 모드

    private val undoStack = ArrayDeque<Command>()
    private val redoStack = ArrayDeque<Command>()

    fun setEditor(treeViewEditor: TreeViewEditor) {
        editor = treeViewEditor
        editor.requestMoveNodeByDragging(true) // 화면 드래그 활성화
    }

    override fun onCreateViewHolder(parent: ViewGroup, node: TreeNode) =
        TreeViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_mindmap_node, parent, false), node
        )

    // TODO: 노드 커스터마이즈
    override fun onBindViewHolder(holder: TreeViewHolder<MindMapNodeData>) {
        holder.view.findViewById<TextView>(R.id.node_title).text = holder.node.value.content

        holder.view.setOnClickListener {
            selected = holder.node

            if (editMode) {
                // 편집 모드일 때: 노드 추가 다이얼로그

            } else {
                // 편집 모드 아닐 때: 상세 내용 보기 다이어로그

            }
            onNodeClick(holder.node)

        }

        // 편집 모드 일 때만 동작
        holder.view.setOnLongClickListener {
            if (editMode) {
                // 수정, 삭제 팝업 메뉴
                // 수정 누를 시 작성되어 있던 내용 다이얼로그
                // 삭제 누를 시 (타이틀 이름) 삭제하시겠습니까 다이얼로그

            }
            true
        }
    }

    override fun onDrawLine(drawInfo: DrawInfo?): BaseLine? = null

    fun addChildToSelected() {
        selected?.let { parent ->
            val child = TreeNode(MindMapNodeData(UUID.randomUUID().toString(), ""))
            perform(AddNodeCommand(editor, parent, child))
        }
    }

    fun removeSelected() {
        selected?.let { node ->
            val parent = node.parentNode ?: return
            perform(RemoveNodeCommand(editor, node, parent))
        }
    }

    fun focusCenter() {
        editor.focusMidLocation()
    }

    fun perform(command: Command) {
        command.redo()
        undoStack.addLast(command)
        redoStack.clear()
    }

    fun undo() {
        if (undoStack.isNotEmpty()) {
            val command = undoStack.removeLast()
            command.undo()
            redoStack.addLast(command)
        }
    }

    fun redo() {
        if (redoStack.isNotEmpty()) {
            val command = redoStack.removeLast()
            command.redo()
            undoStack.addLast(command)
        }
    }

    fun changeEditMode(state: Boolean) {
        editMode = state
        editor.requestMoveNodeByDragging(state)
    }
}
