package com.hihihihi.gureumpage.ui.mindmap.mapper

import com.hihihihi.domain.model.MindmapNode
import com.hihihihi.gureumpage.ui.mindmap.model.MindMapNodeData

fun MindmapNode.toUi(): MindMapNodeData =
    MindMapNodeData(
        id = mindmapNodeId,
        title = nodeTitle,
        content = nodeEx,
        icon = icon,
        color = color?.toLong(),
        bookImage = bookImage,
    )

fun MindMapNodeData.toDomain(mindmapId: String, parentId: String?): MindmapNode =
    MindmapNode(
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