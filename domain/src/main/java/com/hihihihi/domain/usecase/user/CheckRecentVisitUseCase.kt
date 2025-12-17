package com.hihihihi.domain.usecase.user

import com.hihihihi.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class CheckRecentVisitUseCase @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
) {
    suspend operator fun invoke(thresholdMillis: Long): Boolean {
        val lastVisit = userPreferencesRepository.lastVisitFlow.first()
        if (lastVisit == 0L) return true    // 처음 방문이면 알림 보냄

        val diff =  System.currentTimeMillis() - lastVisit
        return diff in 1 until thresholdMillis // 7일 이내까지 알림 보냄
    }
}