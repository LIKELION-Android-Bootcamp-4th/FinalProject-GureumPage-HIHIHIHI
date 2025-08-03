package com.hihihihi.data.repotisoryimpl

import com.hihihihi.data.remote.datasource.UserBookRemoteDataSource
import com.hihihihi.data.remote.mapper.toDomain
import com.hihihihi.domain.model.UserBook
import com.hihihihi.domain.repository.UserBookRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserBookRepositoryImpl @Inject constructor(
    private val userBookRemoteDataSource: UserBookRemoteDataSource
): UserBookRepository {
    override fun getUserBooks(userId: String): Flow<List<UserBook>> {
        return userBookRemoteDataSource.getUserBooks(userId)
            .map { dtoList -> dtoList.map { it.toDomain() } }
    }
}