package com.hihihihi.domain.usecase.user

import com.hihihihi.domain.repository.UserPreferencesRepository
import com.hihihihi.domain.util.runSuspendCatching
import javax.inject.Inject

class SetLastProviderUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    suspend operator fun invoke(provider: String): Result<Unit> = runSuspendCatching {
        repository.setLastProvider(provider)
    }
}
