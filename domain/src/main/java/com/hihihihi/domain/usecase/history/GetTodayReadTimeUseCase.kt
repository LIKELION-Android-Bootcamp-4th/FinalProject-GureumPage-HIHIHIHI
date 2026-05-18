package com.hihihihi.domain.usecase.history

import com.hihihihi.domain.repository.HistoryRepository
import com.hihihihi.domain.util.runSuspendCatching
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetTodayReadTimeUseCase @Inject constructor(
    private val historyRepository: HistoryRepository,
) {
    suspend operator fun invoke(userId: String): Result<Int> = runSuspendCatching {
        historyRepository.getTodayHistoriesByUserId(userId)
            .first()
            .sumOf { it.readTime }
    }
}
