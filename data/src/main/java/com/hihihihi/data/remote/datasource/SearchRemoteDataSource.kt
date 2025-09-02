package com.hihihihi.data.remote.datasource

import com.hihihihi.data.remote.dto.BookDetailDto
import com.hihihihi.data.remote.dto.SearchBookDto

interface SearchRemoteDataSource {
    suspend fun searchBooks(query: String, startIndex: Int, maxResults: Int): List<SearchBookDto>
    suspend fun getBookDetail(isbn: String): BookDetailDto?
} 