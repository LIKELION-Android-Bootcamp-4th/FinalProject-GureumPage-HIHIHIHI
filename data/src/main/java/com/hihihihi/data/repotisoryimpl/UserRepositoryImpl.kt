package com.hihihihi.data.repotisoryimpl

import com.hihihihi.data.mapper.toDomain
import com.hihihihi.data.remote.datasource.UserRemoteDataSource
import com.hihihihi.domain.model.User
import com.hihihihi.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val remote: UserRemoteDataSource
) : UserRepository{

    override suspend fun getUser(userId: String): User? {
        val dto = remote.getUser(userId) ?: return null
        return dto.toDomain()
    }

    override fun getUserFlow(userId: String): Flow<User> {
        return remote.getUserFlow(userId).map { it.toDomain() }
    }


    override suspend fun updateNickname(userId: String, nickname: String) {
        remote.updateNickname(userId, nickname)
    }

    override suspend fun updateDailyGoalTime(userId: String, dailyGoalTime: Int) {
        remote.updateDailyGoalTime(userId, dailyGoalTime)
    }



}