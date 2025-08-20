package com.hihihihi.data.remote.datasourceimpl

import com.hihihihi.data.remote.datasource.SearchRemoteDataSource
import com.hihihihi.data.remote.dto.BookDetailDto
import com.hihihihi.data.remote.dto.SearchBookDto
import com.hihihihi.data.remote.service.SearchApiService
import javax.inject.Inject

class SearchRemoteDataSourceImpl @Inject constructor(
    private val searchApiService: SearchApiService,
    private val apiKey: String
) : SearchRemoteDataSource {

    override suspend fun searchBooks(query: String): List<SearchBookDto> {
        return try {
            val response = searchApiService.searchBooks(apiKey, query)
            response.body()?.books ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getBookDetail(isbn: String): BookDetailDto? {
        return try {
            val response = searchApiService.getBookDetail(apiKey, itemId = isbn)
            response.body()
        } catch (e: Exception) {
            null
        }
    }
}
