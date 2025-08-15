package com.hihihihi.domain.repository

import com.hihihihi.domain.model.History
import kotlinx.coroutines.flow.Flow
import kotlin.time.Instant

interface HistoryRepository {
    fun getHistoriesByUserBookId(userBookId: String): Flow<List<History>>

    fun getHistoriesByUserId(userId: String): Flow<List<History>>
}