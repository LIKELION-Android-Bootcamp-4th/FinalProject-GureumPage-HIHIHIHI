package com.hihihihi.domain.repository

interface KakaoAuthRepository {
    suspend fun login(): String
}