package com.hihihihi.presentation.ui.mindmap

import android.content.res.Configuration
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hihihihi.presentation.R
import com.hihihihi.presentation.designsystem.theme.GureumPageTheme
import com.hihihihi.presentation.designsystem.theme.GureumTheme
import io.github.hanhyo.composemindmap.canvas.MindMapCanvasState
import io.github.hanhyo.composemindmap.canvas.MindMapNodeVisualState
import io.github.hanhyo.composemindmap.canvas.PayloadMindMapCanvas
import io.github.hanhyo.composemindmap.canvas.rememberMindMapCanvasState
import io.github.hanhyo.composemindmap.model.MindMapNode
import io.github.hanhyo.composemindmap.model.MindMapNodeWithPayload
import io.github.hanhyo.composemindmap.model.MindMapStyle
import com.hihihihi.presentation.designsystem.components.BookCoverImage
import com.hihihihi.presentation.ui.mindmap.sheet.NodeDetailBottomSheet
import com.hihihihi.presentation.ui.mindmap.sheet.NodeEditBottomSheet
import kotlin.math.roundToInt

@Composable
fun MindMapScreen(
    mindmapId: String,
    onNavigateBack: () -> Unit = {},
    viewModel: MindMapViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val canvasState = rememberMindMapCanvasState()
    val context = LocalContext.current

    var pendingEditNode by remember { mutableStateOf<MindMapNode?>(null) }
    var pendingParentId by remember { mutableStateOf<String?>(null) }
    var showEditSheet by remember { mutableStateOf(false) }
    var deleteTargetId by remember { mutableStateOf<String?>(null) }
    var detailNode by remember { mutableStateOf<MindMapNode?>(null) }
    var showExitEditDialog by remember { mutableStateOf(false) }
    val content = uiState as? MindMapUiState.Content

    val requestNavigateBack = {
        if (content?.editMode == true) {
            showExitEditDialog = true
        } else {
            onNavigateBack()
        }
    }

    BackHandler(enabled = content?.editMode == true) {
        showExitEditDialog = true
    }

    LaunchedEffect(mindmapId) {
        viewModel.load(mindmapId)
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is MindMapEffect.ShowToast ->
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                is MindMapEffect.ShowNodeEditSheet -> {
                    pendingEditNode = effect.node
                    pendingParentId = effect.parentId
                    showEditSheet = true
                }
                is MindMapEffect.ShowDeleteConfirm -> {
                    deleteTargetId = effect.nodeId
                }
                is MindMapEffect.ShowNodeDetail -> {
                    detailNode = effect.node
                }
                MindMapEffect.NavigateBack -> {
                    showExitEditDialog = false
                    onNavigateBack()
                }
            }
        }
    }

    MindMapContent(
        uiState = uiState,
        canvasState = canvasState,
        onEvent = viewModel::onEvent,
        onNavigateBack = requestNavigateBack,
        onRequestEditNode = { node ->
            pendingEditNode = node
            pendingParentId = null
            showEditSheet = true
        },
        onRequestDeleteNode = { deleteTargetId = it },
    )

    // 노드 편집/추가 BottomSheet
    if (showEditSheet) {
        NodeEditBottomSheet(
            node = pendingEditNode,
            onSave = { title, subtitle, icon, color ->
                showEditSheet = false
                val node = pendingEditNode
                if (node == null) {
                    viewModel.onEvent(
                        MindMapEvent.AddNode(
                            parentId = pendingParentId,
                            title = title,
                            subtitle = subtitle,
                            icon = icon,
                            color = color,
                        )
                    )
                } else {
                    viewModel.onEvent(
                        MindMapEvent.UpdateNode(
                            nodeId = node.id,
                            title = title,
                            subtitle = subtitle,
                            icon = icon,
                            color = color,
                        )
                    )
                }
            },
            onDismiss = { showEditSheet = false },
        )
    }

    // 노드 정보 BottomSheet (비편집 모드)
    val currentDetailNode = detailNode
    if (currentDetailNode != null) {
        NodeDetailBottomSheet(
            node = currentDetailNode,
            onDismiss = { detailNode = null },
        )
    }

    // 노드 삭제 확인 Dialog
    val targetId = deleteTargetId
    if (targetId != null) {
        DeleteConfirmDialog(
            onConfirm = {
                viewModel.onEvent(MindMapEvent.DeleteNode(targetId))
                deleteTargetId = null
            },
            onDismiss = { deleteTargetId = null },
        )
    }

    if (showExitEditDialog) {
        ExitEditConfirmDialog(
            onConfirm = {
                showExitEditDialog = false
                viewModel.onEvent(MindMapEvent.SaveAndNavigateBack)
            },
            onDismiss = { showExitEditDialog = false },
        )
    }
}

@Composable
private fun NodeOverlayToolbar(
    touchOffset: Offset,
    containerSize: IntSize,
    onEdit: () -> Unit,
    onDelete: (() -> Unit)?,
) {
    val colors = GureumTheme.colors
    var toolbarSize by remember { mutableStateOf(IntSize.Zero) }
    Surface(
        modifier = Modifier
            .onSizeChanged { toolbarSize = it }
            .absoluteOffset {
                val margin = 12.dp.roundToPx()
                val maxX = (containerSize.width - toolbarSize.width).coerceAtLeast(0)
                val maxY = (containerSize.height - toolbarSize.height).coerceAtLeast(0)
                val x = (touchOffset.x - toolbarSize.width / 2f).roundToInt().coerceIn(0, maxX)
                val above = (touchOffset.y - toolbarSize.height - margin).roundToInt()
                val below = (touchOffset.y + margin).roundToInt()
                val y = (if (above < 0) below else above).coerceIn(0, maxY)
                IntOffset(x, y)
            },
        shape = RoundedCornerShape(24.dp),
        shadowElevation = 4.dp,
        color = colors.card,
    ) {
        Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)) {
            TextButton(onClick = onEdit) {
                Text("수정", color = colors.primary, fontWeight = FontWeight.Medium)
            }
            if (onDelete != null) {
                TextButton(onClick = onDelete) {
                    Text("삭제", color = colors.systemRed, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
private fun DeleteConfirmDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val colors = GureumTheme.colors
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "노드 삭제",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.gray900,
            )
        },
        text = {
            Text(
                text = "이 노드와 하위 노드를 모두 삭제할까요?",
                fontSize = 14.sp,
                color = colors.gray600,
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("삭제", color = colors.systemRed, fontWeight = FontWeight.Medium)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소", color = colors.gray500, fontWeight = FontWeight.Medium)
            }
        },
        containerColor = colors.card,
    )
}

@Composable
private fun ExitEditConfirmDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val colors = GureumTheme.colors
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "편집 종료",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.gray900,
            )
        },
        text = {
            Text(
                text = "변경사항을 저장하고 나갈까요?",
                fontSize = 14.sp,
                color = colors.gray600,
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("저장 후 나가기", color = colors.primary, fontWeight = FontWeight.Medium)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소", color = colors.gray500, fontWeight = FontWeight.Medium)
            }
        },
        containerColor = colors.card,
    )
}

@Composable
private fun MindMapContent(
    uiState: MindMapUiState,
    canvasState: MindMapCanvasState,
    onEvent: (MindMapEvent) -> Unit,
    onNavigateBack: () -> Unit = {},
    onRequestEditNode: (MindMapNode) -> Unit = {},
    onRequestDeleteNode: (String) -> Unit = {},
) {
    when (uiState) {
        MindMapUiState.Loading -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) { CircularProgressIndicator(color = GureumTheme.colors.primary) }
        is MindMapUiState.Content -> MindMapSuccessContent(
            content = uiState,
            canvasState = canvasState,
            onEvent = onEvent,
            onNavigateBack = onNavigateBack,
            onRequestEditNode = onRequestEditNode,
            onRequestDeleteNode = onRequestDeleteNode,
        )
        is MindMapUiState.Error -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) { Text(uiState.message) }
    }
}

@Composable
private fun MindMapSuccessContent(
    content: MindMapUiState.Content,
    canvasState: MindMapCanvasState,
    onEvent: (MindMapEvent) -> Unit,
    onNavigateBack: () -> Unit = {},
    onRequestEditNode: (MindMapNode) -> Unit = {},
    onRequestDeleteNode: (String) -> Unit = {},
) {
    var touchOffset by remember { mutableStateOf(Offset.Zero) }
    var containerSize by remember { mutableStateOf(IntSize.Zero) }
    val style = MindMapStyle(
        defaultNodeColor = GureumTheme.colors.card,
        selectedStrokeColor = GureumTheme.colors.primary,
        edgeColor = GureumTheme.colors.dividerDeep,
    )

    Scaffold(
        containerColor = GureumTheme.colors.background,
        topBar = {
            MindMapTopBar(onNavigateBack = onNavigateBack)
        },
        bottomBar = {
            MindMapBottomBar(
                editMode = content.editMode,
                canUndo = content.canUndo,
                canRedo = content.canRedo,
                onToggleEdit = { checked ->
                    onEvent(if (checked) MindMapEvent.StartEdit else MindMapEvent.EndEdit)
                },
                onCenter = { canvasState.centerRoot() },
                onUndo = { onEvent(MindMapEvent.Undo) },
                onRedo = { onEvent(MindMapEvent.Redo) },
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .onSizeChanged { containerSize = it },
        ) {
            PayloadMindMapCanvas(
                nodes = content.nodes,
                state = canvasState,
                style = style,
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        awaitEachGesture {
                            touchOffset = awaitFirstDown(requireUnconsumed = false).position
                        }
                    },
                selectedNodeId = content.selectedNodeId,
                editMode = content.editMode,
                nodeSize = { item ->
                    if (item.node.parentId == null && !item.payload.bookImage.isNullOrBlank()) {
                        DpSize(width = 112.dp, height = 148.dp)
                    } else {
                        DpSize(width = style.nodeWidth, height = style.nodeHeight)
                    }
                },
                nodeContent = { item, visualState ->
                    GureumMindMapNode(node = item.node, payload = item.payload, visualState = visualState)
                },
                onNodeClick = { onEvent(MindMapEvent.NodeTapped(it)) },
                onNodeLongClick = { onEvent(MindMapEvent.NodeLongPressed(it)) },
                onCanvasClick = { onEvent(MindMapEvent.CanvasTapped) },
                onAddChildClick = { onEvent(MindMapEvent.ShowAddChildSheet(it)) },
                onNodeMove = { nodeId, newParentId -> onEvent(MindMapEvent.MoveNode(nodeId, newParentId)) },
            )

            val selectedNode = content.selectedNodeId?.let { id ->
                content.nodes.firstOrNull { it.node.id == id }?.node
            }
            if (content.editMode && selectedNode != null) {
                NodeOverlayToolbar(
                    touchOffset = touchOffset,
                    containerSize = containerSize,
                    onEdit = { onRequestEditNode(selectedNode) },
                    onDelete = if (canDeleteMindMapNode(selectedNode)) {
                        { onRequestDeleteNode(selectedNode.id) }
                    } else {
                        null
                    },
                )
            }
        }
    }
}

@Composable
private fun GureumMindMapNode(
    node: MindMapNode,
    payload: GureumMindMapPayload,
    visualState: MindMapNodeVisualState,
) {
    val colors = GureumTheme.colors
    val borderColor = when {
        visualState.isDropTarget -> Color(0xFF4CAF50)
        visualState.isSelected -> colors.primary
        else -> Color.Transparent
    }
    val alpha = when {
        visualState.isDragGhost -> 0.6f
        visualState.isDragging -> 0.3f
        else -> 1f
    }
    val shape = RoundedCornerShape(12.dp)

    if (node.parentId == null && !payload.bookImage.isNullOrBlank()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .alpha(alpha)
                .clip(shape)
                .background(colors.card)
                .border(2.dp, borderColor, shape)
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            BookCoverImage(
                imageUrl = payload.bookImage,
                modifier = Modifier
                    .size(width = 72.dp, height = 96.dp)
                    .clip(RoundedCornerShape(8.dp)),
            )
            Text(
                text = node.title,
                color = colors.gray700,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
            )
        }
    } else {
        val nodeColor = node.color ?: colors.card
        val isLightNode = nodeColor.luminance() > 0.5f
        val titleColor = if (isLightNode) Color(0xFF1F1E18) else colors.white
        val subtitleColor = if (isLightNode) Color(0xFF4F4F41) else Color(0xFFE0E0D8)
        Row(
            modifier = Modifier
                .fillMaxSize()
                .alpha(alpha)
                .clip(shape)
                .background(nodeColor)
                .border(2.dp, borderColor, shape)
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            node.icon?.let { Text(text = it, fontSize = 14.sp) }
            Column {
                Text(
                    text = node.title,
                    color = titleColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = if (node.subtitle.isBlank()) 2 else 1,
                )
                if (node.subtitle.isNotBlank()) {
                    Text(text = node.subtitle, color = subtitleColor, fontSize = 11.sp, maxLines = 1)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MindMapTopBar(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = GureumTheme.colors
    TopAppBar(
        title = {
            Text(
                text = "마인드맵",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.gray900,
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_left),
                    contentDescription = "뒤로가기",
                    tint = colors.gray900,
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colors.card,
            titleContentColor = colors.gray900,
        ),
        modifier = modifier,
    )
}

@Composable
private fun MindMapBottomBar(
    editMode: Boolean,
    canUndo: Boolean,
    canRedo: Boolean,
    onToggleEdit: (Boolean) -> Unit,
    onCenter: () -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
) {
    val colors = GureumTheme.colors
    Surface(
        color = colors.gray150,
        shadowElevation = 8.dp,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding(),
        ) {
            if (editMode) {
                Row(modifier = Modifier.align(Alignment.CenterStart)) {
                    IconButton(onClick = onUndo, enabled = canUndo) {
                        Icon(
                            painter = painterResource(R.drawable.ic_undo),
                            contentDescription = "실행 취소",
                            tint = if (canUndo) colors.gray700 else colors.gray400,
                        )
                    }
                    IconButton(onClick = onRedo, enabled = canRedo) {
                        Icon(
                            painter = painterResource(R.drawable.ic_redo),
                            contentDescription = "다시 실행",
                            tint = if (canRedo) colors.gray700 else colors.gray400,
                        )
                    }
                }
            }
            IconButton(
                onClick = onCenter,
                modifier = Modifier.align(Alignment.Center),
            ) {
                Icon(
                    imageVector = Icons.Default.CenterFocusStrong,
                    contentDescription = "중앙으로 이동",
                    tint = colors.gray700,
                )
            }
            IconToggleButton(
                checked = editMode,
                onCheckedChange = onToggleEdit,
                modifier = Modifier.align(Alignment.CenterEnd),
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = if (editMode) "편집 모드 종료" else "편집 모드 시작",
                    tint = if (editMode) colors.primary else colors.gray700,
                )
            }
        }
    }
}

@Preview(name = "Loading", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun MindMapLoadingPreview() {
    GureumPageTheme {
        MindMapContent(
            uiState = MindMapUiState.Loading,
            canvasState = MindMapCanvasState(),
            onEvent = {},
        )
    }
}

@Preview(name = "With Nodes", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "With Nodes Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun MindMapWithNodesPreview() {
    val sampleNodes = listOf(
        MindMapNodeWithPayload(MindMapNode(id = "root", title = "데미안", subtitle = "헤르만 헤세"), GureumMindMapPayload(null)),
        MindMapNodeWithPayload(MindMapNode(id = "c1", title = "싱클레어", subtitle = "주인공", parentId = "root"), GureumMindMapPayload(null)),
        MindMapNodeWithPayload(MindMapNode(id = "c2", title = "데미안", subtitle = "인도자", parentId = "root"), GureumMindMapPayload(null)),
        MindMapNodeWithPayload(MindMapNode(id = "c3", title = "에바 부인", subtitle = "구원", parentId = "c1"), GureumMindMapPayload(null)),
    )
    GureumPageTheme {
        MindMapContent(
            uiState = MindMapUiState.Content(nodes = sampleNodes),
            canvasState = MindMapCanvasState(),
            onEvent = {},
        )
    }
}

@Preview(name = "Edit Mode", showBackground = true)
@Composable
private fun MindMapEditModePreview() {
    val sampleNodes = listOf(
        MindMapNodeWithPayload(MindMapNode(id = "root", title = "데미안", icon = "📚"), GureumMindMapPayload(null)),
        MindMapNodeWithPayload(MindMapNode(id = "c1", title = "싱클레어", parentId = "root"), GureumMindMapPayload(null)),
        MindMapNodeWithPayload(MindMapNode(id = "c2", title = "데미안", parentId = "root"), GureumMindMapPayload(null)),
    )
    GureumPageTheme {
        MindMapContent(
            uiState = MindMapUiState.Content(
                nodes = sampleNodes,
                editMode = true,
                selectedNodeId = "c1",
                canUndo = true,
            ),
            canvasState = MindMapCanvasState(),
            onEvent = {},
        )
    }
}
