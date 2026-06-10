package com.hihihihi.presentation.ui.mindmap.mapper

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.hihihihi.domain.model.MindmapNode
import com.hihihihi.presentation.ui.mindmap.model.MindMapNodeData

fun MindmapNode.toUi(): MindMapNodeData =
    MindMapNodeData(
        userId = userId,
        id = mindmapNodeId,
        title = nodeTitle,
        content = nodeEx,
        icon = icon,
        color = color?.toStoredColorLongOrNull(),
        bookImage = bookImage,
    )

private fun String.toStoredColorLongOrNull(): Long? =
    toLongOrNull() ?: toULongOrNull()?.let { Color(it).toArgb().toLong() }

fun MindMapNodeData.toDomain(mindmapId: String, parentId: String?): MindmapNode =
    MindmapNode(
        userId = userId,
        mindmapNodeId = id,
        mindmapId = mindmapId,
        nodeTitle = title,
        nodeEx = content.orEmpty(),
        parentNodeId = parentId,
        color = color?.toString(),
        icon = icon,
        deleted = false,
        bookImage = bookImage,
    )
