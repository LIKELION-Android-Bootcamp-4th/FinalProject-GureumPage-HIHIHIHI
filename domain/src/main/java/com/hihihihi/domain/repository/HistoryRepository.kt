package com.hihihihi.domain.repository

import com.hihihihi.domain.model.History
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {
    fun getHistoriesByUserBookId(userBookId: String): Flow<List<History>>

    fun getHistoriesByUserId(userId: String): Flow<List<History>>

    fun getTodayHistoriesByUserId(userId: String): Flow<List<History>>

    suspend fun addHistory(history: History, currentPage: Int): Result<Unit>
}