package com.hihihihi.data.model

import com.hihihihi.data.remote.dto.MindmapNodeDto

sealed interface NodeEditOperation {
    // 편집 세션에서 발생한 변경을 누적 적용하기 위한 DTO 전용 EditOperator
    data class Add(val node: MindmapNodeDto) : NodeEditOperation
    data class Update(val node: MindmapNodeDto) : NodeEditOperation
    data class Delete(val nodeId: String) : NodeEditOperation
}