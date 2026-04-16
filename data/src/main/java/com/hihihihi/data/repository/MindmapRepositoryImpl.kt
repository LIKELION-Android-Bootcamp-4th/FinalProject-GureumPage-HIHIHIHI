package com.hihihihi.data.repository

import com.hihihihi.data.mapper.toDto
import com.hihihihi.data.remote.datasource.MindmapRemoteDataSource
import com.hihihihi.domain.model.Mindmap
import com.hihihihi.domain.repository.MindmapRepository
import javax.inject.Inject

class MindmapRepositoryImpl @Inject constructor(
    private val remoteDataSource: MindmapRemoteDataSource,
) : MindmapRepository {

    override suspend fun createMindmap(mindmap: Mindmap) = remoteDataSource.createMindmap(mindmap.toDto())

}
