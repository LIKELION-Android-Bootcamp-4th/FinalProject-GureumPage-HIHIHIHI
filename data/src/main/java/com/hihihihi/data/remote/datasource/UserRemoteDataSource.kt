package com.hihihihi.data.remote.datasource

import com.hihihihi.data.remote.dto.UserDto
import kotlinx.coroutines.flow.Flow

interface UserRemoteDataSource {
    suspend fun getUser(userId: String): UserDto? //단일 문서 조회
    fun getUserFlow(userId: String): Flow<UserDto> // 단일 문서 조회 (Flow)
    suspend fun updateNickname(userId: String, nickname: String) // 닉네임 필드만 업데이트
    suspend fun updateDailyGoalTime(userId: String, dailyGoalTime: Int) // 일일 목표 시간 필드만 업데이트
}