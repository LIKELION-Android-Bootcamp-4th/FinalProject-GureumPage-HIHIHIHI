package com.hihihihi.gureumpage.ui.mindmap

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.gyso.treeview.TreeViewEditor
import com.gyso.treeview.adapter.DrawInfo
import com.gyso.treeview.adapter.TreeViewAdapter
import com.gyso.treeview.adapter.TreeViewHolder
import com.gyso.treeview.line.BaseLine
import com.gyso.treeview.model.NodeModel
import com.hihihihi.gureumpage.R
import com.hihihihi.gureumpage.databinding.DialogAddNodeBinding
import java.util.UUID
import androidx.core.graphics.drawable.toDrawable
import com.gyso.treeview.model.TreeModel

typealias TreeNode = NodeModel<MindMapNodeData>

class MindMapAdapter(
    val context: Context,
    var onNodeAction: ((node: TreeNode, action: NodeAction) -> Unit)? = null,
) : TreeViewAdapter<MindMapNodeData>() {
    enum class NodeAction { EDIT, DELETE }

    private lateinit var editor: TreeViewEditor // 마인드 맵 편집 객체
    private var selected: TreeNode? = null      // 현재 선택된 노드
    private var editMode: Boolean = false       // 편집 모드

    private val undoTreeStack = ArrayDeque<TreeModel<MindMapNodeData>>()
    private val redoTreeStack = ArrayDeque<TreeModel<MindMapNodeData>>()


    fun setEditor(treeViewEditor: TreeViewEditor) {
        editor = treeViewEditor
        editor.requestMoveNodeByDragging(false) // 화면 드래그 활성화 여부
    }

    override fun onCreateViewHolder(parent: ViewGroup, node: TreeNode) =
        TreeViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_mindmap_node, parent, false), node
        )

    override fun onBindViewHolder(holder: TreeViewHolder<MindMapNodeData>) {
        holder.view.findViewById<TextView>(R.id.node_title).text = holder.node.value.title

        // 노드 클릭 시 이벤트
        holder.view.setOnClickListener {
            selected = holder.node
            if (editMode) { // 편집 모드면 노드 추가 다이얼로그
                showAddNodeDialog(holder.view.context, holder.node) { newNode ->
                    val child = NodeModel(newNode)
                    performAdd(holder.node, child)
                }
            } else { // 편집 모드 아니면 상세 내용 보기 다이얼로그(or 바텀 시트)
//                onNodeClick(holder.node)
            }
        }

        // 노드 롱클릭 시 이벤트(편집 모드 시)
        holder.view.setOnLongClickListener {
            if (!editMode) return@setOnLongClickListener true

            showNodePopup(
                holder.view,
                onEdit = { onNodeAction?.invoke(holder.node, NodeAction.EDIT) },
                onDelete = { onNodeAction?.invoke(holder.node, NodeAction.DELETE) }
            )
            true
        }
    }

    override fun onDrawLine(drawInfo: DrawInfo?): BaseLine? = null

    // 현재 보고있는 트리 복제
    private fun snapshotBeforeChange() {
        (treeModel)?.let {
            undoTreeStack.addLast(cloneTreeModel(it))
            redoTreeStack.clear()
        }
    }

    fun performAdd(parent: NodeModel<MindMapNodeData>, child: NodeModel<MindMapNodeData>) {
        snapshotBeforeChange()
        editor.addChildNodes(parent, child)
        notifyDataSetChange()
    }

    fun performDelete(node: NodeModel<MindMapNodeData>) {
        snapshotBeforeChange()
        editor.removeNode(node)
        notifyDataSetChange()
    }

    // 원본 TreeModel 전체를 끝까지 복제해서 반환
    fun cloneTreeModel(original: TreeModel<MindMapNodeData>): TreeModel<MindMapNodeData> {
        val origRoot = original.rootNode
        val newRoot = NodeModel(origRoot.value.copy()) // 원본 루트 복제
        val newModel = TreeModel(newRoot)       // 복제본을 새 트리노드로 구성
        val idToNew = mutableMapOf<String, NodeModel<MindMapNodeData>>().also {
            it[origRoot.value.id] = newRoot
        }

        // 전체 서브트리 복제
        fun cloneChildren(oldNode: NodeModel<MindMapNodeData>) {
            val parentId = oldNode.value.id
            val newParent = idToNew[parentId]!!
            for (child in oldNode.childNodes) {
                val newChild = NodeModel(child.value.copy())    //노드 값 복제
                newModel.addNode(newParent, newChild)           // 새 모델에 추가
                idToNew[child.value.id] = newChild                  // 매핑에 등록
                cloneChildren(child)                            // 재귀
            }
        }

        cloneChildren(origRoot)
        return newModel
    }

    fun focusCenter() {
        editor.focusMidLocation()
    }

    fun undo() {
        if (undoTreeStack.isNotEmpty()) {
            val prevModel = undoTreeStack.removeLast()
            treeModel?.let { redoTreeStack.addLast(cloneTreeModel(it)) } // 현재 모델도 redo용으로 저장
            setTreeModel(prevModel)
        }
    }

    fun redo() {
        if (redoTreeStack.isNotEmpty()) {
            val nextModel = redoTreeStack.removeLast()
            treeModel?.let { undoTreeStack.addLast(cloneTreeModel(it)) } // 현재 모델을 undo용으로 복제
            setTreeModel(nextModel)
        }
    }

    fun changeEditMode(state: Boolean) {
        editMode = state
        editor.requestMoveNodeByDragging(state)
    }

    // 노드 추가 다이얼로그 아이콘 셀렉터
    fun updateIconSelection(container: LinearLayout, selectedView: View) {
        for (i in 0 until container.childCount) {
            val child = container.getChildAt(i)
            child.alpha = if (child == selectedView) 1f else 0.4f
        }
    }

    // 노드 추가 다이얼로그 색상 셀렉터
    fun updateColorSelection(container: LinearLayout, selectedView: View) {
        for (i in 0 until container.childCount) {
            val child = container.getChildAt(i)
            val bg = child.background as GradientDrawable
            if (child == selectedView) {
                bg.setStroke(container.context.dpToPx(2), ContextCompat.getColor(context, R.color.primary))
            } else {
                bg.setStroke(0, ContextCompat.getColor(context, R.color.background10))
            }
        }
    }

    fun showNodePopup(anchor: View, onEdit: () -> Unit, onDelete: () -> Unit) {
        val context = anchor.context
        val popupView = LayoutInflater.from(context).inflate(R.layout.popup_node_menu, null)
        val pw = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        ).apply {
            elevation = context.dpToPx(4).toFloat()
            setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        }
        popupView.findViewById<TextView>(R.id.menu_edit).setOnClickListener {
            pw.dismiss()
            onEdit()
        }
        popupView.findViewById<TextView>(R.id.menu_delete).setOnClickListener {
            pw.dismiss()
            onDelete()
        }
        pw.showAsDropDown(anchor, context.dpToPx(100), -anchor.height) // 팝업 거리 조절
    }

    fun showAddNodeDialog(context: Context, existingNode: TreeNode? = null, onSave: (MindMapNodeData) -> Unit) {
        // AndroidViewBinding 경유할 때 테마를 못 찾을 수 있음
        val themedInflater = LayoutInflater.from(ContextThemeWrapper(context, R.style.Theme_GureumPage))
        val binding = DialogAddNodeBinding.inflate(themedInflater)
        val dialog = AlertDialog.Builder(context)
            .setView(binding.root)
            .create()
        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())

        var selectedIconRes: Int = -1
        var selectedColor: Int = Color.TRANSPARENT

        fun updateSaveEnabled() {
            val saveBtn = binding.btnSave
            saveBtn.isEnabled =
                binding.editTitle.text!!.isNotBlank() &&
                        binding.editContent.text!!.isNotBlank()
            if (saveBtn.isEnabled) {
                saveBtn.setTextColor(ContextCompat.getColor(context, R.color.white))
                saveBtn.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.button_active_bacground_shape))
            } else {
                saveBtn.setTextColor(ContextCompat.getColor(context, R.color.gray400))
                saveBtn.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.button_background_shape))
            }
        }

        // 아이콘 리스트
        val icons = listOf(R.drawable.ic_close, R.drawable.ic_close)
        icons.forEach { resId ->
            val iv = ImageView(context).apply {
                setImageResource(resId)
                val size = context.dpToPx(32)
                layoutParams = LinearLayout.LayoutParams(size, size).apply {
                    marginEnd = context.dpToPx(8)
                }
                setOnClickListener {
                    selectedIconRes = resId
                    updateIconSelection(binding.iconContainer, this)
                    updateSaveEnabled()
                }
            }
            binding.iconContainer.addView(iv)
        }

        // 색상 리스트
        val colors = listOf(0xFFE57373, 0xFF81C784)
        colors.forEach { colorInt ->
            val v = View(context).apply {
                setBackgroundResource(R.drawable.bg_button_rounded)
                (background as GradientDrawable).setColor(colorInt.toInt())
                val size = context.dpToPx(32)
                layoutParams = LinearLayout.LayoutParams(size, size).apply {
                    marginEnd = context.dpToPx(8)
                }
                setOnClickListener {
                    selectedColor = colorInt.toInt()
                    updateColorSelection(binding.colorContainer, this)
                    updateSaveEnabled()
                }
            }
            binding.colorContainer.addView(v)
        }

        binding.editTitle.addTextChangedListener { updateSaveEnabled() }
        binding.editContent.addTextChangedListener { updateSaveEnabled() }

        binding.btnClose.setOnClickListener { dialog.dismiss() }

        binding.btnSave.setOnClickListener {
            val node = MindMapNodeData(
                id = existingNode?.value?.id ?: UUID.randomUUID().toString(),
                title = binding.editTitle.text.toString(),
                content = binding.editContent.text.toString(),
                bookImage = selectedIconRes.toString()
            )
            onSave(node)
            dialog.dismiss()
        }
        dialog.show()
    }
}

fun Context.dpToPx(dp: Int): Int =
    (dp * resources.displayMetrics.density).toInt()