package com.hihihihi.domain.usecase.user

import com.hihihihi.domain.repository.UserPreferencesRepository
import com.hihihihi.domain.util.runSuspendCatching
import javax.inject.Inject

class SetOnboardingCompleteUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    suspend operator fun invoke(userId: String, complete: Boolean): Result<Unit> = runSuspendCatching {
        repository.setOnboardingComplete(userId, complete)
    }
}
