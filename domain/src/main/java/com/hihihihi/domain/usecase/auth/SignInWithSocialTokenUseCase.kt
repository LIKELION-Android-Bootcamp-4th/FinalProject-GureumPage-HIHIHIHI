package com.hihihihi.domain.usecase.auth

import com.hihihihi.domain.repository.AuthRepository
import com.hihihihi.domain.util.runSuspendCatching
import javax.inject.Inject

enum class SocialProvider { KAKAO, NAVER, GOOGLE }

class SignInWithSocialTokenUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(provider: SocialProvider, token: String): Result<Unit> = runSuspendCatching {
        when (provider) {
            SocialProvider.KAKAO -> authRepository.kakaoLogin(token)
            SocialProvider.NAVER -> authRepository.naverLogin(token)
            SocialProvider.GOOGLE -> authRepository.googleLogin(token)
        }
    }
}
