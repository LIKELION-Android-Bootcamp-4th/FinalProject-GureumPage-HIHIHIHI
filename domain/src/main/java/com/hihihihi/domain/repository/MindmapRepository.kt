package com.hihihihi.domain.repository

import com.hihihihi.domain.model.Mindmap

interface MindmapRepository {
    suspend fun createMindmap(mindmap: Mindmap): Result<Unit>

    suspend fun getMindmap(mindmapId: String): Mindmap

    suspend fun updateMindmap(mindmap: Mindmap): Result<Unit>
}