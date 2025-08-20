package com.hihihihi.domain.repository

import com.hihihihi.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getUser(userId: String): User? //유저 조회
    fun getUserFlow(userId: String): Flow<User> //유저 조회 (Flow)
    suspend fun updateNickname(userId: String, nickname: String) // 닉네임 변경
    suspend fun updateDailyGoalTime(userId: String, dailyGoalTime: Int) // 일일 목표 시간 변경
}