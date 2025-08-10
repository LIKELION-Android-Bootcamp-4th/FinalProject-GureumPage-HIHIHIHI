package com.hihihihi.domain.repository

import com.hihihihi.domain.model.User

interface UserRepository {
    suspend fun getUser(userId: String): User? //유저 조회
    suspend fun updateNickname(userId: String, nickname: String) // 닉네임 변경
}