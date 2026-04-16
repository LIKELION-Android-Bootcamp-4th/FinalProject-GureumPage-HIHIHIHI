package com.hihihihi.domain.usecase.user

import com.hihihihi.domain.repository.UserRepository
import com.hihihihi.domain.util.runSuspendCatching
import javax.inject.Inject

class UpdateNicknameUseCase @Inject constructor(
    private val repo: UserRepository
) {
    suspend operator fun invoke(userId: String, nickname: String): Result<Unit> = runSuspendCatching {
        repo.updateNickname(userId, nickname)
    }
}
