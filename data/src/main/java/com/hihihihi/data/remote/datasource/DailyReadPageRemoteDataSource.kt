package com.hihihihi.data.remote.datasource

import com.hihihihi.data.remote.dto.DailyReadPageDto

interface DailyReadPageRemoteDataSource {
    suspend fun getDailyReadPages(uid: String): List<DailyReadPageDto>
}