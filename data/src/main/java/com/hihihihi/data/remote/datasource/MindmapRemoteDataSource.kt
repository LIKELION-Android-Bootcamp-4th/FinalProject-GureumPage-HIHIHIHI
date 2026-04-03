package com.hihihihi.data.remote.datasource

import com.hihihihi.data.remote.dto.MindmapDto

interface MindmapRemoteDataSource {

    suspend fun createMindmap(mindmapDto: MindmapDto)

}
