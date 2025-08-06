package com.hihihihi.gureumpage.ui.mindmap

data class MindMapNodeData(
    val id: String,
    val title: String,
    val content: String? = null,
    val bookImage: String? = null,  // 루트 노드 용 책 이미지
    val icon: Int? = null,          // 아이콘 처리 방법 생각 필요 // TODO: 추후 enum 변경 고려
)