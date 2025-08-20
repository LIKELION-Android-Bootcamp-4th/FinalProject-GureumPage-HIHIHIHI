package com.hihihihi.data.repotisoryimpl

import com.hihihihi.data.mapper.toDomain
import com.hihihihi.data.remote.datasource.DailyReadPageRemoteDataSource
import com.hihihihi.domain.model.DailyReadPage
import com.hihihihi.domain.repository.DailyReadPageRepository
import javax.inject.Inject

class DailyReadPageRepositoryImpl @Inject constructor(
    private val remote: DailyReadPageRemoteDataSource
) : DailyReadPageRepository {

    override suspend fun getDailyReadPages(userId: String): List<DailyReadPage> {
        return remote.getDailyReadPages(userId).map { it.toDomain() }
    }
}