package com.hihihihi.domain.usecase.auth

import com.hihihihi.domain.repository.AuthRepository
import com.hihihihi.domain.repository.KakaoAuthRepository
import javax.inject.Inject

class SignInWithKakaoUseCase @Inject constructor(
    private val kakaoAuthRepository: KakaoAuthRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke() {
        val accessToken = kakaoAuthRepository.login()
        authRepository.kakaoLogin(accessToken)
    }
}