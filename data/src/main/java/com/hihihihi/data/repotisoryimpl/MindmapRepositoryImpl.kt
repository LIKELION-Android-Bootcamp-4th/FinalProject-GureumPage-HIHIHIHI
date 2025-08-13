package com.hihihihi.data.repotisoryimpl

import com.hihihihi.data.mapper.toDomain
import com.hihihihi.data.mapper.toDto
import com.hihihihi.data.remote.datasource.MindmapRemoteDataSource
import com.hihihihi.domain.model.Mindmap
import com.hihihihi.domain.repository.MindmapRepository
import javax.inject.Inject

class MindmapRepositoryImpl @Inject constructor(
    private val remoteDataSource: MindmapRemoteDataSource,
) : MindmapRepository {
    override suspend fun createMindmap(mindmap: Mindmap): Result<Unit> = remoteDataSource.createMindmap(mindmap.toDto())

    override suspend fun getMindmap(mindmapId: String): Mindmap = remoteDataSource.getMindmap(mindmapId).toDomain()

    override suspend fun updateMindmap(mindmap: Mindmap): Result<Unit> = remoteDataSource.updateMindmap(mindmap.toDto())

}