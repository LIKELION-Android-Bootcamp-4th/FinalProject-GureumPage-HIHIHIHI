package com.hihihihi.domain.usecase.user

import com.hihihihi.domain.model.User
import com.hihihihi.domain.repository.UserRepository
import javax.inject.Inject

class GetUserUseCase @Inject constructor(
    private val repo: UserRepository
) {
    suspend operator fun invoke(userId: String): User? = repo.getUser(userId)
}