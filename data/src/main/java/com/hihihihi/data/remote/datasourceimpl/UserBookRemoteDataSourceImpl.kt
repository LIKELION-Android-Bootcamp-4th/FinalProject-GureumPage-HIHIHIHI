package com.hihihihi.data.remote.datasourceimpl

import com.google.firebase.firestore.FirebaseFirestore
import com.hihihihi.data.remote.datasource.UserBookRemoteDataSource
import com.hihihihi.data.remote.dto.UserBookDto
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class UserBookRemoteDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
): UserBookRemoteDataSource{
    override fun getUserBooks(userId: String): Flow<List<UserBookDto>> = callbackFlow{
        val listenerRegistration = firestore.collection("user_books")
            .whereEqualTo("user_id", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val userBooks = snapshot?.documents?.mapNotNull { document ->
                    document.toObject(UserBookDto::class.java)?.apply {
                        userBookId = document.id  // 문서 ID를 가져와 userBookId 필드에 넣기
                    }
                } ?: emptyList()
                trySend(userBooks)
            }

        awaitClose { listenerRegistration.remove()}
    }

}