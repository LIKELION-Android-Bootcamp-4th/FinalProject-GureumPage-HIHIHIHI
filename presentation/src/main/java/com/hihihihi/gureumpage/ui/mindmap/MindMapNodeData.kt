package com.hihihihi.gureumpage.ui.mindmap

// TODO: 추후 각 마인드 맵에 타이틀이나 이미지 등 추가할지 생각해봐야 함
data class MindMapNodeData(
    val id: String,
    val content: String,
    val colorHex: String = "#FFFFFF"
)