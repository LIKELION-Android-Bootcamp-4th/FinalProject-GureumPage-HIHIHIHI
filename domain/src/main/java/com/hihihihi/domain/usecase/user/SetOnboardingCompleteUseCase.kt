package com.hihihihi.domain.usecase.user

import com.hihihihi.domain.repository.UserPreferencesRepository
import javax.inject.Inject

class SetOnboardingCompleteUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    suspend operator fun invoke(userId: String, complete: Boolean) =
        repository.setOnboardingComplete(userId, complete)
}