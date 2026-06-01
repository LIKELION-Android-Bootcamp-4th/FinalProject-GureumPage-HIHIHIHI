package com.hihihihi.presentation.ui.mindmap

import android.content.res.ColorStateList
import android.content.res.Configuration
import android.view.View
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.gyso.treeview.layout.VerticalTreeLayoutManager
import com.gyso.treeview.line.DashLine
import com.gyso.treeview.model.NodeModel
import com.gyso.treeview.model.TreeModel
import com.hihihihi.domain.model.MindmapNode
import com.hihihihi.presentation.R
import com.hihihihi.presentation.databinding.ActivityMindmapBinding
import com.hihihihi.presentation.designsystem.theme.GureumPageTheme
import com.hihihihi.presentation.designsystem.theme.GureumTheme
import com.hihihihi.presentation.ui.mindmap.mapper.toUi

@Composable
fun MindMapScreen(
    mindmapId: String,
    viewModel: MindMapViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val content = uiState.contentOrDefault()

    LaunchedEffect(mindmapId) {
        viewModel.load(mindmapId)
    }

    MindMapContent(
        mindmapId = mindmapId,
        nodes = content.nodes,
        isEditing = content.editing,
        onEndEdit = { list, autoSave -> viewModel.endEdit(list, autoSave) },
        onStartEdit = viewModel::startEdit,
    )
}

@Composable
private fun MindMapContent(
    mindmapId: String,
    nodes: List<MindmapNode>,
    isEditing: Boolean,
    onEndEdit: (List<MindmapNode>, Boolean) -> Unit,
    onStartEdit: () -> Unit,
) {
    val context = LocalContext.current
    val adapter = remember { MindMapAdapter(context) }
    val lineColor = GureumTheme.colors.gray200.toArgb()

    val thumbColors = ColorStateList(
        arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf(-android.R.attr.state_checked)),
        intArrayOf(ContextCompat.getColor(context, R.color.primary), ContextCompat.getColor(context, R.color.gray300)),
    )

    val trackColors = ColorStateList(
        arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf(-android.R.attr.state_checked)),
        intArrayOf(ContextCompat.getColor(context, R.color.primary50), ContextCompat.getColor(context, R.color.gray200)),
    )

    val currentIsEditing by rememberUpdatedState(isEditing)
    val currentOnEndEdit by rememberUpdatedState(onEndEdit)

    DisposableEffect(Unit) {
        onDispose {
            if (currentIsEditing) {
                currentOnEndEdit(adapter.asDomainList(mindmapId), true)
            }
        }
    }

    AndroidViewBinding(
        factory = ActivityMindmapBinding::inflate,
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
    ) {
        if (baseTreeView.adapter !is MindMapAdapter) {
            baseTreeView.adapter = adapter
            baseTreeView.setTreeLayoutManager(
                VerticalTreeLayoutManager(
                    context,
                    50,     // 부모 - 자식 간 거리
                    20,       // 노드 간 거리
                    DashLine(lineColor, 8),
                ),
            )
            adapter.setEditor(baseTreeView.editor) // 에디터 탑재
        }
        adapter.mindmapId = mindmapId

        adapter.onHistoryChanged = { canUndo, canRedo ->
            val enabled = ContextCompat.getColor(context, R.color.primary)
            val disabled = ContextCompat.getColor(context, R.color.gray300)

            ImageViewCompat.setImageTintList(btnUndo, ColorStateList.valueOf(if (canUndo) enabled else disabled))
            ImageViewCompat.setImageTintList(btnRedo, ColorStateList.valueOf(if (canRedo) enabled else disabled))
        }

        // 편집 중이 아닐 때만 원격 스냅샷으로 트리 구성
        if (!isEditing && nodes.isNotEmpty() && !adapter.sameAs(nodes, mindmapId)) {
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
            adapter.setTreeModelAndReset(model)
            adapter.onHistoryChanged?.invoke(false, false)
        }

        // 편집 모드일 때 수정, 삭제 버튼 이벤트 처리
        adapter.onNodeAction = { node, action ->
            when (action) {
                MindMapAdapter.NodeAction.EDIT -> {
                    adapter.showAddNodeDialog("노드 수정", rootFrame.context, node) { newNode ->
                        adapter.performUpdate(node, newNode)
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

        switchEditMode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) onStartEdit() else onEndEdit(adapter.asDomainList(mindmapId), true)

            adapter.changeEditMode(isChecked)
            btnRedo.visibility = if (isChecked) View.VISIBLE else View.INVISIBLE
            btnUndo.visibility = if (isChecked) View.VISIBLE else View.INVISIBLE
        }

        switchEditMode.thumbTintList = thumbColors
        switchEditMode.trackTintList = trackColors
    }
}

// 어댑터와 원격의 트리 비교
private fun MindMapAdapter.sameAs(nodes: List<MindmapNode>, mindmapId: String): Boolean {
    val cur = asDomainList(mindmapId).sortedBy { it.mindmapNodeId }
    val src = nodes.sortedBy { it.mindmapNodeId }
    return cur == src
}

@Preview(name = "Empty - Light", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Empty - Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun MindMapEmptyPreview() {
    GureumPageTheme {
        MindMapContent(
            mindmapId = "preview",
            nodes = emptyList(),
            isEditing = false,
            onEndEdit = { _, _ -> },
            onStartEdit = {},
        )
    }
}

@Preview(name = "WithNodes - Light", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "WithNodes - Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun MindMapWithNodesPreview() {
    val sampleNodes = listOf(
        MindmapNode(
            userId = "user1",
            mindmapNodeId = "root",
            mindmapId = "preview",
            nodeTitle = "데미안",
            nodeEx = "헤르만 헤세",
            parentNodeId = null,
            color = null,
            icon = null,
            deleted = false,
            bookImage = null
        ),
        MindmapNode(
            userId = "user1",
            mindmapNodeId = "child1",
            mindmapId = "preview",
            nodeTitle = "싱클레어",
            nodeEx = "주인공",
            parentNodeId = "root",
            color = null,
            icon = null,
            deleted = false,
            bookImage = null
        ),
        MindmapNode(
            userId = "user1",
            mindmapNodeId = "child2",
            mindmapId = "preview",
            nodeTitle = "데미안",
            nodeEx = "인도자",
            parentNodeId = "root",
            color = null,
            icon = null,
            deleted = false,
            bookImage = null
        ),
        MindmapNode(
            userId = "user1",
            mindmapNodeId = "grandchild1",
            mindmapId = "preview",
            nodeTitle = "에바 부인",
            nodeEx = "구원",
            parentNodeId = "child1",
            color = null,
            icon = null,
            deleted = false,
            bookImage = null
        )
    )
    GureumPageTheme {
        MindMapContent(
            mindmapId = "preview",
            nodes = sampleNodes,
            isEditing = false,
            onEndEdit = { _, _ -> },
            onStartEdit = {},
        )
    }
}
