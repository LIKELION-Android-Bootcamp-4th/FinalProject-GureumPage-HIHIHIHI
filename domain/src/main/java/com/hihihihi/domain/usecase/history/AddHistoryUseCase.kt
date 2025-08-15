package com.hihihihi.domain.usecase.history

import com.hihihihi.domain.model.History
import com.hihihihi.domain.repository.HistoryRepository
import com.hihihihi.domain.repository.UserBookRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject


class AddHistoryUseCase @Inject constructor(
    private val historyRepository: HistoryRepository,
    private val userBookRepository: UserBookRepository
) {
    suspend operator fun invoke(history: History): Result<Unit> {
        return try {
            historyRepository.addHistory(history)

            val userBook = userBookRepository.getUserBook(history.userBookId).first()
            val newCurrentPage = (userBook.currentPage) + history.readPageCount

            userBookRepository.patchUserBook(
                userBook.copy(currentPage = newCurrentPage)
            ).getOrThrow()

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}