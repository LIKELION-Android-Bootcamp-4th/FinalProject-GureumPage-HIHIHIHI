package com.hihihihi.data.remote.datasource

import com.hihihihi.data.remote.dto.DailyReadPageDto
import com.hihihihi.data.remote.dto.HistoryDto
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface DailyReadPageRemoteDataSource {
    fun getDailyReadPages(userId: String): Flow<List<DailyReadPageDto>>

    fun getDailyReadPagesByUserIdAndDate(userId: String, dayOfStart: Date): Flow<List<DailyReadPageDto>>
}