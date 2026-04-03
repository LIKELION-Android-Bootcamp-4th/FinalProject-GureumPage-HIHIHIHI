package com.hihihihi.domain.usecase.auth

import com.hihihihi.domain.repository.AuthRepository
import javax.inject.Inject

enum class SocialProvider { KAKAO, NAVER, GOOGLE }

class SignInWithSocialTokenUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(provider: SocialProvider, accessToken: String) {
        when (provider) {
            SocialProvider.KAKAO -> authRepository.kakaoLogin(accessToken)
            SocialProvider.NAVER -> authRepository.naverLogin(accessToken)
            SocialProvider.GOOGLE -> authRepository.googleLogin(accessToken)
        }
    }
}
