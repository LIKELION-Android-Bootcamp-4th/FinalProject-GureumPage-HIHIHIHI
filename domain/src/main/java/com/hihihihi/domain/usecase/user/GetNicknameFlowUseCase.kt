package com.hihihihi.domain.usecase.user

import com.hihihihi.domain.repository.UserPreferencesRepository
import javax.inject.Inject

class GetNicknameFlowUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    operator fun invoke() = repository.nickname
}