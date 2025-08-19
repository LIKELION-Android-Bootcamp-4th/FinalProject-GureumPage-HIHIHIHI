package com.hihihihi.data.mapper

import com.hihihihi.data.remote.dto.MindmapDto
import com.hihihihi.domain.model.Mindmap

fun MindmapDto.toDomain() = Mindmap(
    userId = userId,
    mindmapId = mindmapId,
    userBookId = userBookId,
    rootNodeId = rootNodeId,
)

fun Mindmap.toDto() = MindmapDto(
    userId = userId,
    mindmapId = mindmapId,
    userBookId = userBookId,
    rootNodeId = rootNodeId,
)
