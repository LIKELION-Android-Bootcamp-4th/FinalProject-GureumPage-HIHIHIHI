package com.hihihihi.data.mapper

import com.hihihihi.data.remote.dto.MindmapNodeDto
import com.hihihihi.domain.model.MindmapNode

fun MindmapNodeDto.toDomain() = MindmapNode(
    mindmapNodeId = mindmapNodeId,
    mindmapId = mindmapId,
    nodeTitle = nodeTitle,
    nodeEx = nodeEx,
    parentNodeId = parentNodeId,
    color = color,
    icon = icon,
    deleted = deleted,
    bookImage = bookImage
)

fun MindmapNode.toDto() = MindmapNodeDto(
    mindmapNodeId = mindmapNodeId,
    mindmapId = mindmapId,
    nodeTitle = nodeTitle,
    nodeEx = nodeEx,
    parentNodeId = parentNodeId,
    color = color,
    icon = icon,
    deleted = deleted,
    bookImage = bookImage,
)