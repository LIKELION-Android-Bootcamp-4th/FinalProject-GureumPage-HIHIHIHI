package com.hihihihi.domain.usecase.history

import com.hihihihi.domain.model.History
import com.hihihihi.domain.model.RecordType
import com.hihihihi.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetTodayReadTimeUseCaseTest {

    @Test
    fun returnsZeroWhenThereAreNoTodayHistories() = runBlocking {
        val useCase = GetTodayReadTimeUseCase(FakeHistoryRepository(emptyList()))

        val result = useCase("user-id")

        assertEquals(0, result.getOrThrow())
    }

    @Test
    fun returnsSumOfTodayHistoryReadTime() = runBlocking {
        val histories = listOf(
            history(readTime = 600),
            history(readTime = 900),
        )
        val useCase = GetTodayReadTimeUseCase(FakeHistoryRepository(histories))

        val result = useCase("user-id")

        assertEquals(1500, result.getOrThrow())
    }

    @Test
    fun returnsFailureWhenTodayHistoriesQueryFails() = runBlocking {
        val exception = IllegalStateException("history query failed")
        val useCase = GetTodayReadTimeUseCase(FailingHistoryRepository(exception))

        val result = useCase("user-id")

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    private fun history(readTime: Int): History =
        History(
            id = "",
            userId = "user-id",
            userBookId = "user-book-id",
            date = null,
            startTime = null,
            endTime = null,
            readTime = readTime,
            readPageCount = 0,
            recordType = RecordType.TIMER,
        )

    private class FakeHistoryRepository(
        private val todayHistories: List<History>,
    ) : HistoryRepository {
        override fun getHistoriesByUserBookId(userBookId: String): Flow<List<History>> =
            error("Not used")

        override fun getHistoriesByUserId(userId: String): Flow<List<History>> =
            error("Not used")

        override fun getTodayHistoriesByUserId(userId: String): Flow<List<History>> =
            flowOf(todayHistories)

        override suspend fun addHistory(history: History, currentPage: Int) {
            error("Not used")
        }
    }

    private class FailingHistoryRepository(
        private val exception: Throwable,
    ) : HistoryRepository {
        override fun getHistoriesByUserBookId(userBookId: String): Flow<List<History>> =
            error("Not used")

        override fun getHistoriesByUserId(userId: String): Flow<List<History>> =
            error("Not used")

        override fun getTodayHistoriesByUserId(userId: String): Flow<List<History>> =
            flow { throw exception }

        override suspend fun addHistory(history: History, currentPage: Int) {
            error("Not used")
        }
    }
}
