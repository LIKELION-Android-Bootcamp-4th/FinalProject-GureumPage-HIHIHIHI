package com.hihihihi.data.mapper

import com.hihihihi.data.remote.dto.MindmapDto
import com.hihihihi.domain.model.Mindmap

fun MindmapDto.toDomain() = Mindmap(
    mindmapId = mindmapId,
    userbookId = userBookId
)

fun Mindmap.toDto() = MindmapDto(
    mindmapId = mindmapId,
    userBookId = userbookId
)
