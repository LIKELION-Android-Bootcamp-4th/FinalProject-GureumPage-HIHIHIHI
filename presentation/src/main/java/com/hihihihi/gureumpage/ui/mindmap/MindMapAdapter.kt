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
import coil3.load
import coil3.request.crossfade
import coil3.request.transformations
import coil3.transform.RoundedCornersTransformation
import com.gyso.treeview.model.TreeModel
import com.hihihihi.domain.model.MindmapNode
import com.hihihihi.gureumpage.databinding.DialogNodeDetailBinding
import com.hihihihi.gureumpage.ui.mindmap.model.MindMapNodeData

typealias TreeNode = NodeModel<MindMapNodeData>

class MindMapAdapter(
    val context: Context,
    var onNodeAction: ((node: TreeNode, action: NodeAction) -> Unit)? = null,
) : TreeViewAdapter<MindMapNodeData>() {
    enum class NodeAction { EDIT, DELETE }

    private lateinit var editor: TreeViewEditor // 마인드 맵 편집 객체
    private var editMode: Boolean = false       // 편집 모드

    var mindmapId: String = ""

    private val undoTreeStack = ArrayDeque<TreeModel<MindMapNodeData>>()
    private val redoTreeStack = ArrayDeque<TreeModel<MindMapNodeData>>()

    // Editor 설정
    fun setEditor(treeViewEditor: TreeViewEditor) {
        editor = treeViewEditor
        editor.requestMoveNodeByDragging(false) // 화면 드래그 활성화 여부
    }

    override fun onCreateViewHolder(parent: ViewGroup, node: TreeNode) =
        TreeViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_mindmap_node, parent, false), node
        )

    override fun onBindViewHolder(holder: TreeViewHolder<MindMapNodeData>) {
        val node = holder.node.value
        val titleView = holder.view.findViewById<TextView>(R.id.node_title)
        val iconView = holder.view.findViewById<TextView>(R.id.node_icon)
        val colorView = holder.view.findViewById<View>(R.id.node_color)
        val coverView = holder.view.findViewById<ImageView>(R.id.node_book_image)

        titleView.text = node.title

        // 책 커버(루트 노드)여부
        val showCover = !node.bookImage.isNullOrBlank() && holder.node.parentNode == null
        if (showCover) {
            coverView.visibility = View.VISIBLE
            coverView.load(node.bookImage) {
                crossfade(true)
                transformations(RoundedCornersTransformation(10f))
            }
            iconView.visibility = View.GONE
            colorView.visibility = View.GONE
        } else {
            coverView.visibility = View.GONE
            node.color?.let { colorLong ->
                val colorInt = (colorLong and 0xFFFFFFFF).toInt()
                (colorView.background.mutate() as GradientDrawable).setColor(colorInt)
                colorView.visibility = View.VISIBLE
            } ?: run { colorView.visibility = View.GONE }

            if (!node.icon.isNullOrEmpty()) {
                iconView.visibility = View.VISIBLE
                iconView.text = node.icon
            } else {
                iconView.visibility = View.GONE
            }
        }

        // 노드 클릭 시 이벤트
        holder.view.setOnClickListener {
            if (editMode) { // 편집 모드면 노드 추가 다이얼로그
                showAddNodeDialog(holder.view.context, null) { newNode ->
                    val child = NodeModel(newNode)
                    performAdd(holder.node, child)
                }
            } else { // 편집 모드 아니면 상세 내용 보기 다이얼로그
                showDetailDialog(holder.view.context, holder.node)
            }
        }

        // 노드 롱클릭 시 이벤트(편집 모드 시)
        holder.view.setOnLongClickListener {
            if (!editMode) return@setOnLongClickListener true

            showNodePopup(
                holder.view,
                onEdit = {
                    onNodeAction?.invoke(holder.node, NodeAction.EDIT)
                    snapshotBeforeChange()
                },
                onDelete = {
                    onNodeAction?.invoke(holder.node, NodeAction.DELETE)
                }
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

    // 노드 추가를 스택에 추가
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

    // 편집 모드로 변경 함수
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
                bg.setStroke(container.context.dpToPx(3), ContextCompat.getColor(context, R.color.primary))
            } else {
                bg.setStroke(0, ContextCompat.getColor(context, R.color.background10))
            }
        }
    }

    // 수정 삭제 팝업
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

    // 노드 추가 수정 다이얼로그
    fun showAddNodeDialog(context: Context, existingNode: TreeNode? = null, onSave: (MindMapNodeData) -> Unit) {
        // AndroidViewBinding 경유할 때 테마를 못 찾을 수 있음
        val themedInflater = LayoutInflater.from(ContextThemeWrapper(context, R.style.Theme_GureumPage))
        val binding = DialogAddNodeBinding.inflate(themedInflater)
        val dialog = AlertDialog.Builder(context)
            .setView(binding.root)
            .create()
        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())

        var selectedIcon: String? = existingNode?.value?.icon
        var selectedColor: Long? = existingNode?.value?.color

        if (existingNode != null) {
            binding.editTitle.setText(existingNode.value.title)
            binding.editContent.setText(existingNode.value.content ?: "")

            binding.iconContainer.findViewWithTag<ImageView>(selectedIcon)?.let {
                updateIconSelection(binding.iconContainer, it)
            }

            binding.colorContainer.findViewWithTag<View>(selectedColor)?.let {
                updateColorSelection(binding.colorContainer, it)
            }
        }

        fun updateSaveEnabled() {
            val saveBtn = binding.btnSave
            saveBtn.isEnabled =
                binding.editTitle.text!!.isNotBlank()
            if (saveBtn.isEnabled) {
                saveBtn.setTextColor(ContextCompat.getColor(context, R.color.white))
                saveBtn.setBackgroundDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.button_active_bacground_shape
                    )
                )
            } else {
                saveBtn.setTextColor(ContextCompat.getColor(context, R.color.gray400))
                saveBtn.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.button_background_shape))
            }
        }

        // 아이콘 리스트
        val icons = listOf(
            "❤\uFE0F",
            "\uD83C\uDFE0",
            "⏰",
            "\uD83D\uDCDD",
            "\uD83D\uDCA1",
            "✨",
            "\uD83D\uDCCC",
            "\uD83D\uDCC6",
            "\uD83D\uDE21",
            "☺\uFE0F",
            "\uD83D\uDE2D",
            "\uD83D\uDE35",
            "\uD83E\uDD14",
            "\uD83D\uDC75\uD83C\uDFFB",
            "\uD83E\uDDD3\uD83C\uDFFB",
            "\uD83D\uDC69\uD83C\uDFFB",
            "\uD83E\uDDD1\uD83C\uDFFB",
            "\uD83D\uDC76\uD83C\uDFFB",
            "\uD83E\uDDD2\uD83C\uDFFB",
            "\uD83D\uDC67\uD83C\uDFFB",
            "☠\uFE0F"
        )
        icons.forEach { icon ->
            val textIcon = TextView(context).apply {
                text = icon
                val size = context.dpToPx(32)
                layoutParams = LinearLayout.LayoutParams(size, size).apply {
                    marginEnd = context.dpToPx(8)
                    textSize = 24f
                }
                setOnClickListener {
                    selectedIcon = icon
                    updateIconSelection(binding.iconContainer, this)
                    updateSaveEnabled()
                }
            }
            binding.iconContainer.addView(textIcon)
        }

        // 색상 리스트
        val colors = listOf(
            0xFFFF6363,
            0xFFFFA663,
            0xFFFFD051,
            0xFFA1D36E,
            0xFF86B9DB,
            0xFF415D9D,
            0xFFAB8AD1,
            0xFFFFB8C9,
            0xFF504F59,
            0xFFFFFFFF
        )
        colors.forEach { colorInt ->
            val view = View(context).apply {
                setBackgroundResource(R.drawable.bg_button_rounded)
                (background as GradientDrawable).setColor(colorInt.toInt())
                val size = context.dpToPx(32)
                layoutParams = LinearLayout.LayoutParams(size, size).apply {
                    marginEnd = context.dpToPx(8)
                }
                setOnClickListener {
                    selectedColor = colorInt
                    updateColorSelection(binding.colorContainer, this)
                    updateSaveEnabled()
                }
            }
            binding.colorContainer.addView(view)
        }

        binding.editTitle.addTextChangedListener { updateSaveEnabled() }
        binding.editContent.addTextChangedListener { updateSaveEnabled() }

        binding.btnClose.setOnClickListener { dialog.dismiss() }

        binding.btnSave.setOnClickListener {
            val node = MindMapNodeData(
                userId = "",
                id = existingNode?.value?.id ?: UUID.randomUUID().toString(),
                title = binding.editTitle.text.toString(),
                content = binding.editContent.text.toString(),
                icon = selectedIcon,
                color = selectedColor
            )
            onSave(node)
            dialog.dismiss()
        }
        dialog.show()
    }

    // 노드 상세 정보 다이얼로그
    fun showDetailDialog(context: Context, currentNode: TreeNode) {
        val themedInflater = LayoutInflater.from(ContextThemeWrapper(context, R.style.Theme_GureumPage))
        val binding = DialogNodeDetailBinding.inflate(themedInflater)
        val dialog = AlertDialog.Builder(context)
            .setView(binding.root)
            .create()
        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())

        binding.dialogTitle.text = currentNode.value.title
        binding.nodeContent.text = currentNode.value.content

        currentNode.value.color?.toInt()?.let { colorInt ->
            val bg = binding.nodeColor.background.mutate() as GradientDrawable
            bg.setColor(colorInt)
            binding.nodeColor.background = bg
            binding.nodeIconContainer.visibility = View.VISIBLE
        }

        currentNode.value.icon?.let { emoji ->
            binding.nodeIcon.text = emoji
            binding.nodeIconContainer.visibility = View.VISIBLE
        }

        binding.btnClose.setOnClickListener { dialog.dismiss() }

        dialog.show()
    }
}

fun Context.dpToPx(dp: Int): Int =
    (dp * resources.displayMetrics.density).toInt()

// 트리를 스냅샷 리스트로 변환
fun MindMapAdapter.asDomainList(mindmapId: String): List<MindmapNode> {
    val model = this.treeModel ?: return emptyList()
    val nodes = mutableListOf<MindmapNode>()
    fun dfs(node: TreeNode, parentId: String?) {
        val value = node.value
        nodes += MindmapNode(
            userId =value.userId,
            mindmapNodeId = value.id,
            mindmapId = mindmapId,
            nodeTitle = value.title,
            nodeEx = value.content.orEmpty(),
            parentNodeId = parentId,
            color = value.color?.toString(),
            icon = value.icon,
            deleted = false,
            bookImage = value.bookImage
        )
        node.childNodes.forEach { dfs(it, value.id) }
    }
    dfs(model.rootNode, null)
    return nodes
}