package com.hihihihi.data.repotisoryimpl

import com.hihihihi.data.mapper.toDomain
import com.hihihihi.data.remote.datasource.DailyReadPageRemoteDataSource
import com.hihihihi.domain.model.DailyReadPage
import com.hihihihi.domain.model.History
import com.hihihihi.domain.repository.DailyReadPageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject

class DailyReadPageRepositoryImpl @Inject constructor(
    private val remote: DailyReadPageRemoteDataSource
) : DailyReadPageRepository {

    override fun getDailyReadPages(userId: String): Flow<List<DailyReadPage>> {
        return remote.getDailyReadPages(userId)
            .map { dtoList -> dtoList.map { it.toDomain() } }
    }

    override fun getDailyReadPagesByUserIdAndDate(
        uid: String,
        dayOfStart: Date
    ): Flow<List<DailyReadPage>> =
        remote.getDailyReadPagesByUserIdAndDate(uid, dayOfStart)
            .map { dtoList -> dtoList.map { it.toDomain() } }

}