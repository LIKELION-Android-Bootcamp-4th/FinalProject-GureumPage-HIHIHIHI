package com.hihihihi.data.repotisoryimpl

import com.hihihihi.data.remote.datasource.UserBookRemoteDataSource
import com.hihihihi.data.remote.mapper.toDomain
import com.hihihihi.data.remote.mapper.toDto
import com.hihihihi.domain.model.ReadingStatus
import com.hihihihi.domain.model.UserBook
import com.hihihihi.domain.repository.UserBookRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

// UserBookRepository 인터페이스 구현체
class UserBookRepositoryImpl @Inject constructor(
    private val userBookRemoteDataSource: UserBookRemoteDataSource // Firestore 원격 데이터 소스 주입
) : UserBookRepository {
    // 사용자 ID로 책 목록을 가져오는 함수 구현
    override fun getUserBooks(userId: String): Flow<List<UserBook>> {
        return userBookRemoteDataSource.getUserBooks(userId)  // DTO 리스트를 Flow로 받음
            .map { dtoList -> dtoList.map { it.toDomain() } }    // 각 DTO를 도메인 모델로 변환
    }

    override fun getUserBooksByStatus(userId: String, status: ReadingStatus): Flow<List<UserBook>> {
        return userBookRemoteDataSource.getUserBooksByStatus(userId, status)
            .map { dtoList -> dtoList.map { it.toDomain() } }
    }

    override fun getUserBook(userBookId: String): Flow<UserBook> {
        return userBookRemoteDataSource.getUserBook(userBookId).map { it.toDomain() }
    }

    override suspend fun patchUserBook(userBook: UserBook): Result<Unit> {
        val userBookDto = userBook.toDto()

        return userBookRemoteDataSource.patchUserBook(userBookDto)
    }


    override suspend fun addUserBook(userBook: UserBook): Result<String> {
        val userBookDto = userBook.toDto()
        return userBookRemoteDataSource.addUserBook(userBookDto)
    }
}