package com.hihihihi.domain.usecase.user

import com.hihihihi.domain.model.Quote
import com.hihihihi.domain.model.ReadingStatus
import com.hihihihi.domain.model.User
import com.hihihihi.domain.model.UserBook
import com.hihihihi.domain.repository.HistoryRepository
import com.hihihihi.domain.repository.QuoteRepository
import com.hihihihi.domain.repository.UserBookRepository
import com.hihihihi.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetHomeDataUseCase @Inject constructor(
    private val userBookRepository: UserBookRepository,
    private val quoteRepository: QuoteRepository,
    private val historyRepository: HistoryRepository,
    private val userRepository: UserRepository
    ) {
     operator fun invoke(userId: String): Flow<HomeData> {
        val userBooksFlow = userBookRepository.getUserBooksByStatus(userId, ReadingStatus.READING)
        val quotesFlow = quoteRepository.getQuotes(userId)

        val todayReadTimeFlow = historyRepository.getTodayHistoriesByUserId(userId)
            .map { histories ->
                histories.sumOf { it.readTime }
            }

        val userFlow = userRepository.getUserFlow(userId)

        return combine(userBooksFlow, quotesFlow, todayReadTimeFlow ,userFlow) { userBooks, quotes, todayReadTime, user ->
            HomeData(
                userBooks = userBooks,
                quotes = quotes,
                todayReadTime = todayReadTime ,
                user = user
            )
        }
    }

}

data class HomeData(
    val userBooks: List<UserBook>,
    val quotes: List<Quote>,
    val todayReadTime: Int,
    val user: User
)