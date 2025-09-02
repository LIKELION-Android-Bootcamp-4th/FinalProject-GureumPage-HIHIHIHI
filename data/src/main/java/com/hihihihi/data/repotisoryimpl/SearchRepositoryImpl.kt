package com.hihihihi.data.repositoryimpl

import com.hihihihi.data.mapper.toDomain
import com.hihihihi.data.remote.datasource.SearchRemoteDataSource
import com.hihihihi.domain.model.SearchBook
import com.hihihihi.domain.repository.SearchRepository
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val searchRemoteDataSource: SearchRemoteDataSource
) : SearchRepository {

    override suspend fun searchBooks(query: String): List<SearchBook> {
        return searchBooks(query = query, page = 1, pageSize = 10)
    }

    override suspend fun searchBooks(
        query: String,
        page: Int,
        pageSize: Int
    ): List<SearchBook> {
        val startIndex = page
        // ↓ Remote에 startIndex/maxResults 기준으로 추가
        return searchRemoteDataSource.searchBooks(
            query = query,
            startIndex = startIndex,
            maxResults = pageSize
        ).map { it.toDomain() }
    }


    override suspend fun getBookPageCount(isbn: String): Int? {
        return try {
            val bookDetail = searchRemoteDataSource.getBookDetail(isbn)
            bookDetail?.item?.firstOrNull()?.subInfo?.itemPage?.toIntOrNull()
        } catch (e: Exception) {
            null
        }
    }
} 