package com.hihihihi.data.repotisoryimpl

import com.hihihihi.data.remote.datasource.KakaoDataSource
import com.hihihihi.domain.repository.KakaoAuthRepository
import javax.inject.Inject

class KakaoAuthRepositoryImpl @Inject constructor(
    private val kakaoDataSource: KakaoDataSource
): KakaoAuthRepository {
    override suspend fun login(): String {
        return kakaoDataSource.signIn()
    }
}