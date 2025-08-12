package com.hihihihi.data.remote.datasource

import com.hihihihi.data.remote.dto.HistoryDto
import kotlinx.coroutines.flow.Flow

interface HistoryRemoteDataSource {
    fun getHistoriesByUserBookId(userBookId: String): Flow<List<HistoryDto>>
}