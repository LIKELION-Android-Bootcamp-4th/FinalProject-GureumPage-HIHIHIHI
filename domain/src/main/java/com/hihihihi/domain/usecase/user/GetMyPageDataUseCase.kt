package com.hihihihi.domain.usecase.user

import com.hihihihi.domain.model.ReadingStatus
import com.hihihihi.domain.model.User
import com.hihihihi.domain.repository.DailyReadPageRepository
import com.hihihihi.domain.repository.UserBookRepository
import com.hihihihi.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class GetMyPageDataUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val dailyRepository: DailyReadPageRepository,
    private val userBookRepository: UserBookRepository
) {
    operator fun invoke(userId: String): Flow<MyPageData> {
        val userFlow = userRepository.getUserFlow(userId)
        val dailyReadPagesFlow = dailyRepository.getDailyReadPages(userId)
        val userBooksFlow = userBookRepository.getUserBooksByStatus(userId, ReadingStatus.FINISHED)

        return combine(
            userFlow,
            dailyReadPagesFlow,
            userBooksFlow
        ) { user, dailies, userBooks ->
            val readingStats = dailies.associate { it.date to it.totalReadPageCount }

            // 완료 도서 수 계산
            val totalBooks = userBooks.size

            // 총 읽은 페이지 수
            val totalPages = dailies.sumOf { it.totalReadPageCount }

            // 총 독서 시간 (초 -> 분)
            val totalReadMinutes = userBooks.sumOf { (it.totalReadTime / 60).coerceAtLeast(0) }

            MyPageData(
                user = user,
                readingStats = readingStats,
                totalBooks = totalBooks,
                totalPages = totalPages,
                totalReadMinutes = totalReadMinutes
            )
        }
    }
}

data class MyPageData(
    val user: User?,
    val readingStats: Map<LocalDate, Int>,
    val totalBooks: Int,
    val totalPages: Int,
    val totalReadMinutes: Int
)