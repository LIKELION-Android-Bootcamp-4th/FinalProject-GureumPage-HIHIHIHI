package com.hihihihi.data.remote.datasourceimpl

import com.google.firebase.firestore.FirebaseFirestore
import com.hihihihi.data.remote.datasource.UserRemoteDataSource
import com.hihihihi.data.remote.dto.UserDto
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRemoteDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : UserRemoteDataSource {

    private val col get() = firestore.collection("users") //경로

    override suspend fun getUser(userId: String): UserDto? {
        val snap = col.document(userId).get().await()
        val dto = snap.toObject(UserDto::class.java) ?: return null
        dto.userId = snap.id
        return dto
    }

    override suspend fun updateNickname(userId: String, nickname: String) {
        col.document(userId).update("nickname", nickname).await() // 부분 업데이트
    }
}