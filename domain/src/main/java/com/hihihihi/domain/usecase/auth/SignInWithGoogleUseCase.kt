package com.hihihihi.domain.usecase.auth


import com.hihihihi.domain.repository.AuthRepository
import javax.inject.Inject

class SignInWithGoogleUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(data: android.content.Intent?) {
        repository.handleGoogleSignInResult(data)
    }
}