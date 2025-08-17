package com.hihihihi.data.remote.datasourceimpl

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.hihihihi.data.remote.datasource.MindmapRemoteDataSource
import com.hihihihi.data.remote.dto.MindmapDto
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MindmapRemoteDataSourceImpl @Inject constructor(
    private val fireStore: FirebaseFirestore
) : MindmapRemoteDataSource {
    private fun reference(maindMapId: String) =
        fireStore.collection("mindmaps").document(maindMapId)

    override suspend fun createMindmap(mindmapDto: MindmapDto): Result<Unit> = runCatching {
        require(mindmapDto.mindmapId.isNotBlank()) { "마인드맵 ID가 필요합니다." }

        reference(mindmapDto.mindmapId).set(mindmapDto).await()
    }

    override suspend fun getMindmap(mindmapId: String): MindmapDto {
        val collection = reference(mindmapId).get().await()

        return collection.toObject(MindmapDto::class.java)
            ?: throw IllegalArgumentException("마인드맵을 찾을 수 없습니다.")
    }

    override suspend fun updateMindmap(mindmapDto: MindmapDto): Result<Unit> = runCatching {
        require(mindmapDto.mindmapId.isNotBlank()) { "마인드맵 ID가 필요합니다." }

        reference(mindmapDto.mindmapId).set(mindmapDto, SetOptions.merge()).await()
    }
}