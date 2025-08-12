package com.hihihihi.domain.usecase.userbook

import com.hihihihi.domain.model.History
import com.hihihihi.domain.model.Quote
import com.hihihihi.domain.model.UserBook
import com.hihihihi.domain.repository.HistoryRepository
import com.hihihihi.domain.repository.QuoteRepository
import com.hihihihi.domain.repository.UserBookRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetBookDetailDataUseCase @Inject constructor(
    private val repository: UserBookRepository,
    private val quoteRepository: QuoteRepository,
    private val historyRepository: HistoryRepository
){
    operator fun invoke(userBookId: String): Flow<BookDetailData> {
        val userBookFlow = repository.getUserBook(userBookId)
        val quotesFlow = quoteRepository.getQuotesByUserBookId(userBookId).map { it.sortedByDescending { it.createdAt } }
        val historyFlow = historyRepository.getHistoriesByUserBookId(userBookId)

        return combine(userBookFlow, quotesFlow, historyFlow) { userBook, quotes, histories ->
            BookDetailData(userBook, quotes, histories)
        }
    }
}

data class BookDetailData(
    val userBook: UserBook,
    val quotes: List<Quote>,
    val history: List<History>
)