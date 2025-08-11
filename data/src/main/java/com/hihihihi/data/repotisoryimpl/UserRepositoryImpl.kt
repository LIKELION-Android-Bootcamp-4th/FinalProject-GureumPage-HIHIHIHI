package com.hihihihi.data.repotisoryimpl

import com.hihihihi.data.mapper.toDomain
import com.hihihihi.data.remote.datasource.UserRemoteDataSource
import com.hihihihi.domain.model.User
import com.hihihihi.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val remote: UserRemoteDataSource
) : UserRepository{

    override suspend fun getUser(userId: String): User? {
        val dto = remote.getUser(userId) ?: return null
        return dto.toDomain()
    }

    override suspend fun updateNickname(userId: String, nickname: String) {
        remote.updateNickname(userId, nickname)
    }
}