package com.hihihihi.domain.usecase.user

import com.hihihihi.domain.repository.UserPreferencesRepository
import com.hihihihi.domain.util.runSuspendCatching
import javax.inject.Inject

class UpdateLastVisitUseCase @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
) {
    suspend operator fun invoke(): Result<Unit> = runSuspendCatching {
        userPreferencesRepository.updateLastVisit()
    }
}
