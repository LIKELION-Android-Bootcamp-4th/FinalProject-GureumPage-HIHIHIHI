package com.hihihihi.domain.usecase.history

import com.hihihihi.domain.model.History
import com.hihihihi.domain.repository.HistoryRepository
import javax.inject.Inject

class AddHistoryUseCase @Inject constructor(
    private val historyRepository: HistoryRepository,
) {
    suspend operator fun invoke(history: History, currentPage: Int): Result<Unit> {
        return try {
            historyRepository.addHistory(history, currentPage)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}