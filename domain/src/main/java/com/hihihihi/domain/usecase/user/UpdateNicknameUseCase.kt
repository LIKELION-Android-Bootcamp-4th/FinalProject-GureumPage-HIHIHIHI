package com.hihihihi.domain.usecase.user

import com.hihihihi.domain.repository.UserRepository
import javax.inject.Inject

class UpdateNicknameUseCase @Inject constructor(
    private val repo: UserRepository
) {
    suspend operator fun invoke(userId: String, nickname: String) =
        repo.updateNickname(userId, nickname)
}