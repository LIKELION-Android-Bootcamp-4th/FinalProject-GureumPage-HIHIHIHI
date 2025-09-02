package com.hihihihi.data.remote.datasourceimpl

import com.google.firebase.firestore.FirebaseFirestore
import com.hihihihi.data.common.util.ISBNNormalizer
import com.hihihihi.data.remote.datasource.UserBookRemoteDataSource
import com.hihihihi.data.remote.dto.UserBookDto
import com.hihihihi.data.remote.mapper.toMap
import com.hihihihi.domain.model.ReadingStatus
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserBookRemoteDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : UserBookRemoteDataSource {

    override fun getUserBooks(userId: String): Flow<List<UserBookDto>> =
        getUserBooksInternal(userId)

    override fun getUserBooksByStatus(
        userId: String,
        status: ReadingStatus
    ): Flow<List<UserBookDto>> =
        getUserBooksInternal(userId, status)

    // 🔁 공통 로직 추출
    private fun getUserBooksInternal(
        userId: String,
        status: ReadingStatus? = null
    ): Flow<List<UserBookDto>> = callbackFlow {
        var query = firestore.collection("user_books")
            .whereEqualTo("user_id", userId)   // user_id 를 포함하고 있는 문서만 가져옴

        if (status != null) {
            query = query.whereEqualTo("status", status.name.lowercase()) // 입력한 상태의 userbook만 가져옴
        }

        // Firestore의 addSnapshotListener는 데이터 실시간 변경을 감지
        val listenerRegistration = query.addSnapshotListener { snapshot, error ->
            // 에러 발생 시 Flow 종료
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            // snapshot에서 문서 리스트를 UserBookDto 리스트로 변환
            val userBooks = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject(UserBookDto::class.java)?.apply {
                    userBookId = doc.id // 문서 ID를 가져와 userBookId 필드에 넣기
                }
            } ?: emptyList() // 문서가 없으면 빈 리스트
            // 변환된 리스트를 Flow에 발행
            trySend(userBooks)
        }
        FirestoreListenerManager.add(listenerRegistration)
        // Flow가 종료될 때 리스너 해제
        awaitClose { listenerRegistration.remove() }
    }

    override fun getUserBook(userBookId: String): Flow<UserBookDto> = callbackFlow {
        val docRef = firestore.collection("user_books").document(userBookId)

        val listenerRegistration = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            val userBookDto =
                snapshot?.toObject(UserBookDto::class.java)?.copy(userBookId = snapshot.id)

            if (userBookDto != null) {
                trySend(userBookDto).isSuccess
            } else {
                close(Exception("UserBookDto 변환 실패"))
            }
        }
        FirestoreListenerManager.add(listenerRegistration)
        awaitClose { listenerRegistration.remove() }
    }

    override suspend fun patchUserBook(userBookDto: UserBookDto): Result<Unit> = try {
        val docRef = firestore.collection("user_books").document(userBookDto.userBookId)

        docRef.set(userBookDto.toMap()).await()

        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun checkUserBookExists(userId: String, rawIsbn: String): Boolean {
        return try {
            val normalizedIsbn = ISBNNormalizer.normalize(rawIsbn) ?: return false
            val documentId = "${userId}-${normalizedIsbn}"

            val docRef = firestore.collection("user_books").document(documentId)
            val snapshot = docRef.get().await()
            snapshot.exists()
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun addUserBook(
        userId: String,
        rawIsbn: String,
        userBookDto: UserBookDto
    ): Result<String> {
        return try {
            val normalizedIsbn = ISBNNormalizer.normalize(rawIsbn)
                ?: return Result.failure(Exception("유효하지 않은 ISBN입니다"))

            val documentId = "${userId}-${normalizedIsbn}"
            val docRef = firestore.collection("user_books").document(documentId)

            // 이미 존재하는지 확인
            if (docRef.get().await().exists()) {
                Result.failure(Exception("이미 추가된 책입니다"))
            } else {
                // DTO에 정규화된 ISBN 설정
                val normalizedDto = userBookDto.copy(isbn13 = normalizedIsbn)
                docRef.set(normalizedDto.toMap()).await()
                Result.success(documentId)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
