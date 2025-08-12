package com.hihihihi.domain.usecase.user

import com.hihihihi.domain.repository.UserPreferencesRepository
import javax.inject.Inject

class SetNicknameUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    suspend operator fun invoke(userId: String, nickname: String) = repository.setNickname(userId, nickname)
}