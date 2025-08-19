package com.hihihihi.domain.usecase.auth

import android.app.Activity
import com.hihihihi.domain.repository.AuthRepository
import com.hihihihi.domain.repository.NaverAuthRepository
import javax.inject.Inject

class SignInWithNaverUseCase @Inject constructor(
    private val naverAuthRepository: NaverAuthRepository,
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(activity: Activity) {
        val accessToken = naverAuthRepository.login(activity)
        authRepository.naverLogin(accessToken)
    }
}