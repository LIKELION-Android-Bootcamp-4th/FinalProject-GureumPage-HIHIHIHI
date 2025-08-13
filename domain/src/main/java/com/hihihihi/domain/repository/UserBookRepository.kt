package com.hihihihi.domain.repository

import com.hihihihi.domain.model.ReadingStatus
import com.hihihihi.domain.model.UserBook
import kotlinx.coroutines.flow.Flow

// 사용자 책 목록을 제공하는 저장소 인터페이스
interface UserBookRepository {
    // 특정 사용자(userId)의 책 목록을 Flow 형태로 반환
    fun getUserBooks(userId: String): Flow<List<UserBook>>

     fun getUserBooksByStatus(userId: String, status: ReadingStatus): Flow<List<UserBook>>

    fun getUserBook(userBookId: String): Flow<UserBook>

    suspend fun patchUserBook(userBook: UserBook): Result<Unit>

    suspend fun addUserBook(userBook: UserBook): Result<String>
}