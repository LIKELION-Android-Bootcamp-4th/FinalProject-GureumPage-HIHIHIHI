package com.hihihihi.domain.repository

import com.hihihihi.domain.model.DailyReadPage
import com.hihihihi.domain.model.History
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Date

interface DailyReadPageRepository {
    fun getDailyReadPages(userId: String): Flow<List<DailyReadPage>>

    fun getDailyReadPagesByUserIdAndDate(userId: String,dayOfStart: Date): Flow<List<DailyReadPage>>
}