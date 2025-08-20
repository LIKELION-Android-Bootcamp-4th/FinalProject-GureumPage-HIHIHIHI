package com.hihihihi.domain.usecase.user

import com.hihihihi.domain.repository.UserRepository
import javax.inject.Inject

class UpdateDailyGoalTimeUseCase @Inject constructor(
    private val repo: UserRepository
) {
    suspend operator fun invoke(userId: String, dailyGoalTime: Int) =
        repo.updateDailyGoalTime(userId, dailyGoalTime)
}