package com.hihihihi.data.remote.datasource

import com.hihihihi.data.remote.dto.MindmapDto

interface MindmapRemoteDataSource {
    suspend fun createMindmap(mindmapDto: MindmapDto): Result<Unit>

    suspend fun getMindmap(mindmapId: String): MindmapDto

    suspend fun updateMindmap(mindmapDto: MindmapDto): Result<Unit>
}