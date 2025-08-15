package com.hihihihi.data.remote.datasource

import com.hihihihi.data.remote.dto.HistoryDto
import kotlinx.coroutines.flow.Flow

interface HistoryRemoteDataSource {
    fun getHistoriesByUserBookId(userBookId: String): Flow<List<HistoryDto>>

    fun getHistoriesByUserId(userId: String): Flow<List<HistoryDto>>

    suspend fun addHistory(history: HistoryDto, uid: String): Result<Unit>
}