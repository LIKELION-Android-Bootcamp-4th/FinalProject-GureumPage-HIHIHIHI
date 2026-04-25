package com.hihihihi.domain.usecase.search

import com.hihihihi.domain.model.SearchBook
import com.hihihihi.domain.repository.SearchRepository
import com.hihihihi.domain.util.runSuspendCatching
import javax.inject.Inject

class SearchBooksUseCase @Inject constructor(
    private val searchRepository: SearchRepository
) {
    suspend operator fun invoke(query: String): Result<List<SearchBook>> = runSuspendCatching {
        searchRepository.searchBooks(query)
    }

    suspend operator fun invoke(
        query: String,
        page: Int = 1,
        pageSize: Int = 10
    ): Result<List<SearchBook>> = runSuspendCatching {
        searchRepository.searchBooks(
            query = query,
            page = page,
            pageSize = pageSize
        )
    }
} 
