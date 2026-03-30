package com.hihihihi.domain.repository

import com.hihihihi.domain.model.DailyReadPage
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface DailyReadPageRepository {
    fun getDailyReadPages(userId: String): Flow<List<DailyReadPage>>

    fun getDailyReadPagesByUserIdAndDate(userId: String,dayOfStart: Date): Flow<List<DailyReadPage>>
}
