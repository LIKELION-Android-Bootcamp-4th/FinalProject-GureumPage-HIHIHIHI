package com.hihihihi.data.remote.datasource

import com.hihihihi.data.remote.dto.UserDto

interface UserRemoteDataSource {
    suspend fun getUser(userId: String): UserDto? //단일 문서 조회
    suspend fun updateNickname(userId: String, nickname: String) // 닉네임 필드만 업데이트
}