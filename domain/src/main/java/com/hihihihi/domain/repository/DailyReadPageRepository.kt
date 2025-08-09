package com.hihihihi.domain.repository

import com.hihihihi.domain.model.DailyReadPage

interface DailyReadPageRepository {
    suspend fun getDailyReadPages(uid: String): List<DailyReadPage>
}