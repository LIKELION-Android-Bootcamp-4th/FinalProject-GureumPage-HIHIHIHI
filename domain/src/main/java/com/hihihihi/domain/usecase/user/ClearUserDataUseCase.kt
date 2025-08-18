package com.hihihihi.domain.usecase.user

import com.hihihihi.domain.repository.UserPreferencesRepository
import javax.inject.Inject

class ClearUserDataUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    suspend fun clearAll() = repository.clearAll()
}