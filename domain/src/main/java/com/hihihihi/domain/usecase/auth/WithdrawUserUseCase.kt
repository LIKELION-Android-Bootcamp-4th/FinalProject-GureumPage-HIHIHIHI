package com.hihihihi.domain.usecase.auth

import com.hihihihi.domain.repository.AuthRepository
import com.hihihihi.domain.util.runSuspendCatching
import javax.inject.Inject

class WithdrawUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(provider: String): Result<Unit> = runSuspendCatching {
        // 1. 먼저 소셜 플랫폼에서 연결 해제
        when (provider) {
            "kakao" -> authRepository.unlinkKakao()
            "naver" -> authRepository.unlinkNaver()
            // google.com은 특별한 unlink 필요 없음 (Firebase functions에서 처리)
        }

        // 2. Firebase Functions 호출 (Firestore + Auth 삭제)
        authRepository.deleteUserAccount()
    }
}
