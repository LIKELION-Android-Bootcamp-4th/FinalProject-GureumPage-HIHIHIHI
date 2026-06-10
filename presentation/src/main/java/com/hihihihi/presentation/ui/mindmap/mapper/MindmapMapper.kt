package com.hihihihi.presentation.ui.mindmap.mapper

import androidx.compose.ui.graphics.Color
import com.hihihihi.domain.model.MindmapNode
import io.github.hanhyo.composemindmap.model.MindMapNode
import io.github.hanhyo.composemindmap.model.MindMapNodeWithPayload
import io.github.hanhyo.composemindmap.model.withPayload
import com.hihihihi.presentation.ui.mindmap.GureumMindMapPayload

fun MindmapNode.toLibraryModel(): MindMapNodeWithPayload<GureumMindMapPayload> =
    MindMapNode(
        id = mindmapNodeId,
        title = nodeTitle,
        subtitle = nodeEx,
        parentId = parentNodeId,
        color = color?.toULongOrNull()?.let { Color(it) },
        icon = icon,
    ).withPayload(GureumMindMapPayload(bookImage = bookImage))

fun MindMapNode.toDomain(mindmapId: String, userId: String, bookImage: String?): MindmapNode =
    MindmapNode(
        userId = userId,
        mindmapNodeId = id,
        mindmapId = mindmapId,
        nodeTitle = title,
        nodeEx = subtitle,
        parentNodeId = parentId,
        color = color?.value?.toString(),
        icon = icon,
        deleted = false,
        bookImage = bookImage,
    )
