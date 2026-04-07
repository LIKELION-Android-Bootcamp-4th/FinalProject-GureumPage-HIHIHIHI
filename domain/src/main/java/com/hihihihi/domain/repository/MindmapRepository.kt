package com.hihihihi.domain.repository

import com.hihihihi.domain.model.Mindmap

interface MindmapRepository {

    suspend fun createMindmap(mindmap: Mindmap)

}
