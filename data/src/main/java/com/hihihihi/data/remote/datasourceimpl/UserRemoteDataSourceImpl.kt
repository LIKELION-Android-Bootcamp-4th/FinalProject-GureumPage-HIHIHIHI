package com.hihihihi.data.remote.datasourceimpl

import com.google.firebase.firestore.FirebaseFirestore
import com.hihihihi.data.remote.datasource.UserRemoteDataSource
import com.hihihihi.data.remote.dto.UserDto
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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

    override fun getUserFlow(userId: String): Flow<UserDto> = callbackFlow {
        val docRef = firestore.collection("users").document(userId)

        val listenerRegistration = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }


            if (snapshot?.exists() == true) {
                val dto = snapshot.toObject(UserDto::class.java)
                if (dto != null) {
                    dto.userId = snapshot.id
                    trySend(dto).isSuccess
                } else {
                    close(Exception("User 파싱 실패"))
                }
            } else { }
        }
        awaitClose { listenerRegistration.remove() }
    }


    override suspend fun updateNickname(userId: String, nickname: String) {
        col.document(userId).update("nickname", nickname).await() // 부분 업데이트
    }

    override suspend fun updateDailyGoalTime(userId: String, dailyGoalTime: Int) {
        col.document(userId).update("daily_goal_time", dailyGoalTime).await()
    }
}