package com.hihihihi.data.remote.datasource

import com.hihihihi.data.remote.dto.DailyReadPageDto
import com.hihihihi.data.remote.dto.HistoryDto
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface DailyReadPageRemoteDataSource {
    suspend fun getDailyReadPages(userId: String): List<DailyReadPageDto>

    fun getDailyReadPagesByUserIdAndDate(userId: String, dayOfStart: Date): Flow<List<DailyReadPageDto>>
}