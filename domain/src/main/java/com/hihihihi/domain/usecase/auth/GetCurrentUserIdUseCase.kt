package com.hihihihi.domain.usecase.auth

import com.hihihihi.domain.repository.AuthRepository
import javax.inject.Inject

class GetCurrentUserIdUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): String? {
        return authRepository.getCurrentUserId()
    }
}
