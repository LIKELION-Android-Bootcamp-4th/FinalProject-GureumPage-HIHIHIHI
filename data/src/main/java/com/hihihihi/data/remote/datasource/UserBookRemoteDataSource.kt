package com.hihihihi.data.remote.datasource

import com.hihihihi.data.remote.dto.UserBookDto
import com.hihihihi.domain.model.ReadingStatus
import kotlinx.coroutines.flow.Flow

// Firestore 등 원격 데이터 소스에 접근하기 위한 인터페이스 정의
interface UserBookRemoteDataSource {
    // 특정 사용자(userId)의 책 목록을 비동기 스트림(Flow) 형태로 반환
    fun getUserBooks(userId: String): Flow<List<UserBookDto>>

    fun getUserBooksByStatus(userId: String, status: ReadingStatus): Flow<List<UserBookDto>>

    fun getUserBook(userBookId: String): Flow<UserBookDto>

    suspend fun patchUserBook(userBookDto: UserBookDto): Result<Unit>

    suspend fun checkUserBookExists(userId: String, rawIsbn: String): Boolean

    suspend fun addUserBook(userId: String, rawIsbn: String, userBookDto: UserBookDto): Result<String>
}
