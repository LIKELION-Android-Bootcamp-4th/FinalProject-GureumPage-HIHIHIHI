package com.hihihihi.domain.usecase.user

import com.hihihihi.domain.repository.UserPreferencesRepository
import com.hihihihi.domain.util.runSuspendCatching
import javax.inject.Inject

class SetNicknameUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    suspend operator fun invoke(userId: String, nickname: String): Result<Unit> = runSuspendCatching {
        repository.setNickname(userId, nickname)
    }
}
