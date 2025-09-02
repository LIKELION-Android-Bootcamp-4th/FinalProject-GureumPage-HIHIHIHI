package com.hihihihi.domain.repository

import com.hihihihi.domain.model.SearchBook

interface SearchRepository {
    suspend fun searchBooks(query: String): List<SearchBook>
    suspend fun getBookPageCount(isbn: String): Int?
    suspend fun searchBooks(
        query: String,
        page: Int,
        pageSize: Int
    ): List<SearchBook>
} 