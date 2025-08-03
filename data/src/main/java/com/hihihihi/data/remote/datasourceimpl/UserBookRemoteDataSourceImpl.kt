package com.hihihihi.data.remote.datasourceimpl

import com.google.firebase.firestore.FirebaseFirestore
import com.hihihihi.data.remote.datasource.UserBookRemoteDataSource
import com.hihihihi.data.remote.dto.UserBookDto
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class UserBookRemoteDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore // Firestore 인스턴스 주입
): UserBookRemoteDataSource{
    // 특정 userId에 해당하는 user_books 컬렉션 문서들을 Flow로 반환
    override fun getUserBooks(userId: String): Flow<List<UserBookDto>> = callbackFlow{
        val listenerRegistration = firestore.collection("user_books")
            // user_id 를 포함하고 있는 문서만 가져옴
            .whereEqualTo("user_id", userId)
            // Firestore의 addSnapshotListener는 데이터 실시간 변경을 감지
            .addSnapshotListener { snapshot, error ->
                // 에러 발생 시 Flow 종료
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                // snapshot에서 문서 리스트를 UserBookDto 리스트로 변환
                val userBooks = snapshot?.documents?.mapNotNull { document ->
                    document.toObject(UserBookDto::class.java)?.apply {
                        userBookId = document.id  // 문서 ID를 가져와 userBookId 필드에 넣기
                    }
                } ?: emptyList() // 문서가 없으면 빈 리스트
                // 변환된 리스트를 Flow에 발행
                trySend(userBooks)
            }
        // Flow가 종료될 때 리스너 해제
        awaitClose { listenerRegistration.remove()}
    }

}