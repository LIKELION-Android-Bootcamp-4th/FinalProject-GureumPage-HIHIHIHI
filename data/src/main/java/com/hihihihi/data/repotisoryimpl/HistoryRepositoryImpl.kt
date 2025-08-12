package com.hihihihi.data.repotisoryimpl

import com.hihihihi.data.mapper.toDomain
import com.hihihihi.data.remote.datasource.HistoryRemoteDataSource
import com.hihihihi.domain.model.History
import com.hihihihi.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HistoryRepositoryImpl @Inject constructor(
    private val historyRemoteDataSource: HistoryRemoteDataSource
): HistoryRepository {

    override fun getHistoriesByUserBookId(userBookId: String): Flow<List<History>> {
        return historyRemoteDataSource.getHistoriesByUserBookId(userBookId)
            .map { dtoList -> dtoList.map { it.toDomain() } }
    }
}