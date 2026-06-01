package com.hihihihi.presentation.ui.mindmap

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
            }
        }
    }

    MindMapContent(
        uiState = uiState,
        canvasState = canvasState,
        onEvent = viewModel::onEvent,
        onNavigateBack = onNavigateBack,
    )

    // 선택된 노드 위 오버레이 툴바 (편집 모드일 때만)
    val content = uiState as? MindMapUiState.Content
    val selectedId = content?.selectedNodeId
    if (content != null && content.editMode && selectedId != null) {
        val selectedNode = content.nodes.firstOrNull { it.node.id == selectedId }?.node
        if (selectedNode != null) {
            NodeOverlayToolbar(
                onEdit = {
                    viewModel.onEvent(MindMapEvent.NodeTapped(selectedId))
                    pendingEditNode = selectedNode
                    pendingParentId = null
                    showEditSheet = true
                },
                onDelete = if (canDeleteMindMapNode(selectedNode)) {
                    { deleteTargetId = selectedId }
                } else {
                    null
                },
            )
        }
    }

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

    // 노드 삭제 확인 BottomSheet
    val targetId = deleteTargetId
    if (targetId != null) {
        DeleteConfirmBottomSheet(
            onConfirm = {
                viewModel.onEvent(MindMapEvent.DeleteNode(targetId))
                deleteTargetId = null
            },
            onDismiss = { deleteTargetId = null },
        )
    }
}

@Composable
private fun NodeOverlayToolbar(
    onEdit: () -> Unit,
    onDelete: (() -> Unit)?,
) {
    val colors = GureumTheme.colors
    Box(modifier = Modifier.fillMaxSize()) {
        Surface(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 72.dp),
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeleteConfirmBottomSheet(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val colors = GureumTheme.colors
    val sheetState = rememberModalBottomSheetState()
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "노드 삭제",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.gray900,
            )
            HorizontalDivider(color = colors.dividerShallow)
            Text(
                text = "이 노드와 하위 노드를 모두 삭제할까요?",
                fontSize = 14.sp,
                color = colors.gray600,
            )
            Button(
                onClick = onConfirm,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.systemRed,
                    contentColor = colors.white,
                ),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text("삭제", fontWeight = FontWeight.SemiBold)
            }
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.gray700),
            ) {
                Text("취소")
            }
        }
    }
}

@Composable
private fun MindMapContent(
    uiState: MindMapUiState,
    canvasState: MindMapCanvasState,
    onEvent: (MindMapEvent) -> Unit,
    onNavigateBack: () -> Unit = {},
) {
    when (uiState) {
        MindMapUiState.Loading -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) { CircularProgressIndicator(color = GureumTheme.colors.primary) }
        is MindMapUiState.Content -> MindMapSuccessContent(uiState, canvasState, onEvent, onNavigateBack)
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
) {
    val style = MindMapStyle(
        defaultNodeColor = GureumTheme.colors.card,
        selectedStrokeColor = GureumTheme.colors.primary,
        edgeColor = GureumTheme.colors.dividerDeep,
    )

    Scaffold(
        containerColor = GureumTheme.colors.background,
        topBar = {
            MindMapTopBar(
                editMode = content.editMode,
                canUndo = content.canUndo,
                canRedo = content.canRedo,
                onNavigateBack = onNavigateBack,
                onToggleEdit = { checked ->
                    onEvent(if (checked) MindMapEvent.StartEdit else MindMapEvent.EndEdit)
                },
                onCenter = { canvasState.centerRoot() },
                onUndo = { onEvent(MindMapEvent.Undo) },
                onRedo = { onEvent(MindMapEvent.Redo) },
            )
        },
        floatingActionButton = {
            if (content.editMode) {
                FloatingActionButton(
                    onClick = { onEvent(MindMapEvent.ShowAddSheet) },
                    containerColor = GureumTheme.colors.primary,
                    contentColor = GureumTheme.colors.white,
                    elevation = FloatingActionButtonDefaults.elevation(4.dp),
                ) {
                    Icon(Icons.Default.Add, contentDescription = "노드 추가")
                }
            }
        },
    ) { paddingValues ->
        PayloadMindMapCanvas(
            nodes = content.nodes,
            state = canvasState,
            style = style,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .navigationBarsPadding(),
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
        Row(
            modifier = Modifier
                .fillMaxSize()
                .alpha(alpha)
                .clip(shape)
                .background(node.color ?: colors.card)
                .border(2.dp, borderColor, shape)
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            node.icon?.let { Text(text = it, fontSize = 14.sp) }
            Column {
                Text(
                    text = node.title,
                    color = colors.gray900,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = if (node.subtitle.isBlank()) 2 else 1,
                )
                if (node.subtitle.isNotBlank()) {
                    Text(text = node.subtitle, color = colors.gray500, fontSize = 11.sp, maxLines = 1)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MindMapTopBar(
    editMode: Boolean,
    canUndo: Boolean,
    canRedo: Boolean,
    onNavigateBack: () -> Unit,
    onToggleEdit: (Boolean) -> Unit,
    onCenter: () -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
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
        actions = {
            if (editMode) {
                IconButton(onClick = onUndo, enabled = canUndo) {
                    Icon(
                        painter = painterResource(R.drawable.ic_undo),
                        contentDescription = "실행 취소",
                        tint = if (canUndo) colors.gray700 else colors.gray300,
                    )
                }
                IconButton(onClick = onRedo, enabled = canRedo) {
                    Icon(
                        painter = painterResource(R.drawable.ic_redo),
                        contentDescription = "다시 실행",
                        tint = if (canRedo) colors.gray700 else colors.gray300,
                    )
                }
            }
            TextButton(onClick = onCenter) {
                Text("중앙", color = colors.primary, fontWeight = FontWeight.Medium)
            }
            Spacer(Modifier.width(4.dp))
            Switch(
                checked = editMode,
                onCheckedChange = onToggleEdit,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = colors.white,
                    checkedTrackColor = colors.primary,
                    uncheckedThumbColor = colors.white,
                    uncheckedTrackColor = colors.gray200,
                ),
            )
            Spacer(Modifier.width(8.dp))
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colors.card,
            titleContentColor = colors.gray900,
        ),
        modifier = modifier,
    )
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
