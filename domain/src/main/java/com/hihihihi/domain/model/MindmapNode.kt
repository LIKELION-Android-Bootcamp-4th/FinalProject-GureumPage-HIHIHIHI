package com.hihihihi.domain.model

data class MindmapNode(
    val userId: String,
    val mindmapNodeId: String,
    val mindmapId: String,
    val nodeTitle: String,
    val nodeEx: String,
    val parentNodeId: String?,
    val color: String?,
    val icon: String?,
    val deleted: Boolean,
    val bookImage: String?,
)