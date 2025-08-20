package com.hihihihi.data.remote.datasource

interface KakaoDataSource {
    suspend fun signIn(): String
}
