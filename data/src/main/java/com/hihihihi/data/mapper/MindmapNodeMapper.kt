package com.hihihihi.data.mapper

import com.hihihihi.data.remote.dto.MindmapNodeDto
import com.hihihihi.domain.model.MindmapNode
import com.hihihihi.domain.model.MindmapNodeType

fun MindmapNodeDto.toDomain(): MindmapNode = MindmapNode(
    mindmapId = mindmapId,
    nodeId = nodeId,
    nodeTitle = nodeTitle,
    nodeEx = nodeEx,
    nodeParent = nodeParent,
    type = MindmapNodeType.valueOf(type.uppercase())
)

fun MindmapNode.toDto(): MindmapNodeDto = MindmapNodeDto(
    mindmapId = mindmapId,
    nodeId = nodeId,
    nodeTitle = nodeTitle,
    nodeEx = nodeEx,
    nodeParent = nodeParent,
    type = type.name.lowercase()
)