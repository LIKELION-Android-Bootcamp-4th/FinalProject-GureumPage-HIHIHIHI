package com.hihihihi.domain.usecase.search

import android.util.Log
import com.hihihihi.domain.model.SearchBook
import com.hihihihi.domain.repository.SearchRepository
import javax.inject.Inject

class SearchBooksUseCase @Inject constructor(
    private val searchRepository: SearchRepository
) {
    suspend operator fun invoke(query: String): List<SearchBook> {
        val list = searchRepository.searchBooks(query)
        // ✅ 여기! 레포가 실제로 몇 개를 돌려주는지
        Log.d("SearchBooksUseCase", "query=$query, repository.size=${list.size}")
        return list
    }

    suspend operator fun invoke(
        query: String,
        page: Int,
        pageSize: Int
    ): List<SearchBook> {
        val list = searchRepository.searchBooks(query, page, pageSize)
        Log.d("SearchBooksUseCase", "query=$query, page=$page, pageSize=$pageSize, size=${list.size}")
        return list
    }
} 