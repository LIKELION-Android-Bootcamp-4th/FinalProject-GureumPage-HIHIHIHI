package com.hihihihi.domain.usecase.user

import com.hihihihi.domain.repository.UserPreferencesRepository
import javax.inject.Inject

class SetLastProviderUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    suspend operator fun invoke(provider: String) = repository.setLastProvider(provider)
}