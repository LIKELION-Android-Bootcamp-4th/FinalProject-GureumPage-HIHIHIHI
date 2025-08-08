package com.hihihihi.data.repotisoryimpl

import com.hihihihi.data.mapper.toDomain
import com.hihihihi.data.remote.datasource.SearchRemoteDataSource
import com.hihihihi.domain.model.SearchBook
import com.hihihihi.domain.repository.SearchRepository
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val searchRemoteDataSource: SearchRemoteDataSource
) : SearchRepository {

    override suspend fun searchBooks(query: String): List<SearchBook> {
        return searchRemoteDataSource.searchBooks(query)
            .map { it.toDomain() }
    }
} 