package com.hihihihi.domain.model


data class MindmapNode(
    val mindmapId: String,
    val nodeId: String,
    val nodeTitle: String,
    val nodeEx: String,
    val nodeParent: String?,
    val type: MindmapNodeType
)