package com.hihihihi.domain.usecase.user

import com.hihihihi.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetOnboardingCompleteUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    operator fun invoke(userId: String): Flow<Boolean> =
        repository.getOnboardingComplete(userId)
}