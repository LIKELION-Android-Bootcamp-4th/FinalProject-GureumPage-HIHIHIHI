package com.hihihihi.domain.repository

import com.hihihihi.domain.model.History
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {
    fun getHistoriesByUserBookId(userBookId: String): Flow<List<History>>
}