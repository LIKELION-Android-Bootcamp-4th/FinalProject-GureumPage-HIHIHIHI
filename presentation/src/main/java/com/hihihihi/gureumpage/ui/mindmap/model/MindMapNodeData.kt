package com.hihihihi.gureumpage.ui.mindmap.model

data class MindMapNodeData(
    val userId: String,
    val id: String,
    val title: String,
    val content: String? = null,
    val bookImage: String? = null,  // 루트 노드 용 책 이미지
    val icon: String? = null,
    val color: Long? = null
)