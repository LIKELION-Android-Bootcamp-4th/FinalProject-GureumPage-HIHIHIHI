package com.hihihihi.domain.repository

import com.hihihihi.domain.model.DailyReadPage
import com.hihihihi.domain.model.History
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Date

interface DailyReadPageRepository {
    suspend fun getDailyReadPages(userId: String): List<DailyReadPage>

    fun getDailyReadPagesFlow(userId: String): Flow<List<DailyReadPage>> = flow {
        emit(getDailyReadPages(userId))
    }

    fun getDailyReadPagesByUserIdAndDate(userId: String,dayOfStart: Date): Flow<List<DailyReadPage>>
}