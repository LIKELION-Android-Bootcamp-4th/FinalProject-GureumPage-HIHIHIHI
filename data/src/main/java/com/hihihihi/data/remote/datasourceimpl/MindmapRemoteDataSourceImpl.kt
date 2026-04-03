package com.hihihihi.data.remote.datasourceimpl

import com.google.firebase.firestore.FirebaseFirestore
import com.hihihihi.data.remote.datasource.MindmapRemoteDataSource
import com.hihihihi.data.remote.dto.MindmapDto
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MindmapRemoteDataSourceImpl @Inject constructor(
    private val fireStore: FirebaseFirestore
) : MindmapRemoteDataSource {
    private fun reference(maindMapId: String) =
        fireStore.collection("mindmaps").document(maindMapId)

    override suspend fun createMindmap(mindmapDto: MindmapDto) {
        require(mindmapDto.mindmapId.isNotBlank()) { "마인드맵 ID가 필요합니다." }

        reference(mindmapDto.mindmapId).set(mindmapDto).await()
    }

}
