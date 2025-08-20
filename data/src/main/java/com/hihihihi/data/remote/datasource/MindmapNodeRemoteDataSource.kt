package com.hihihihi.data.remote.datasource

import com.hihihihi.domain.operation.NodeEditOperation
import com.hihihihi.data.remote.dto.MindmapNodeDto
import kotlinx.coroutines.flow.Flow

interface MindmapNodeRemoteDataSource {
    // 마인드맵 편집 모드에서 실시간 옵저빙
    fun observe(mindmapId: String): Flow<List<MindmapNodeDto>>

    // 특정 마인드맵의 노드를 로드
    suspend fun loadNodes(mindmapId: String): List<MindmapNodeDto>

    // 편집 모드에서 발생한 변경을 적용
    suspend fun applyNodeOperation(mindmapId: String, operations: List<NodeEditOperation>): Result<Unit>
}
