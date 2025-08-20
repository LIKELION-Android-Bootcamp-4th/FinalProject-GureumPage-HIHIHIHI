package com.hihihihi.domain.model

data class Mindmap(
    val userId: String,
    val mindmapId: String,
    val userBookId: String,
    val rootNodeId: String?,
)