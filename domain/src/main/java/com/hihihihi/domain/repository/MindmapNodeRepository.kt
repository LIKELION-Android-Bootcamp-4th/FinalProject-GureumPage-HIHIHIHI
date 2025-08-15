package com.hihihihi.domain.repository

import com.hihihihi.domain.model.MindmapNode
import com.hihihihi.domain.operation.NodeEditOperation
import kotlinx.coroutines.flow.Flow

interface MindmapNodeRepository {
    fun observe(mindmapId: String): Flow<List<MindmapNode>>

    suspend fun loadNodes(mindmapId: String): List<MindmapNode>

    suspend fun applyNodeOperation(mindmapId: String, operations: List<NodeEditOperation>): Result<Unit>
}