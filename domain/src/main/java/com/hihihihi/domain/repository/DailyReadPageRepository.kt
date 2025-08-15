package com.hihihihi.domain.repository

import com.hihihihi.domain.model.DailyReadPage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface DailyReadPageRepository {
    suspend fun getDailyReadPages(uid: String): List<DailyReadPage>

    fun getDailyReadPagesFlow(uid: String): Flow<List<DailyReadPage>> = flow {
        emit(getDailyReadPages(uid))
    }
}