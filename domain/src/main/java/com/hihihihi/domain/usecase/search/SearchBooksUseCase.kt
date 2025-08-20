package com.hihihihi.domain.usecase.search

import com.hihihihi.domain.model.SearchBook
import com.hihihihi.domain.repository.SearchRepository
import javax.inject.Inject

class SearchBooksUseCase @Inject constructor(
    private val searchRepository: SearchRepository
) {
    suspend operator fun invoke(query: String): List<SearchBook> {
        return searchRepository.searchBooks(query)
    }
} 