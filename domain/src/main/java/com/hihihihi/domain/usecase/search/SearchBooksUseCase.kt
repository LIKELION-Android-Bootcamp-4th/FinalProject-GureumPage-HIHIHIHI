package com.hihihihi.domain.usecase.search

import android.util.Log
import com.hihihihi.domain.model.SearchBook
import com.hihihihi.domain.repository.SearchRepository
import javax.inject.Inject

class SearchBooksUseCase @Inject constructor(
    private val searchRepository: SearchRepository
) {
    suspend operator fun invoke(query: String): List<SearchBook> {
        return searchRepository.searchBooks(query)
    }

    suspend operator fun invoke(
        query: String,
        page: Int = 1,
        pageSize: Int = 10
    ): List<SearchBook> {
        return searchRepository.searchBooks(
            query = query,
            page = page,
            pageSize = pageSize
        )
    }
} 