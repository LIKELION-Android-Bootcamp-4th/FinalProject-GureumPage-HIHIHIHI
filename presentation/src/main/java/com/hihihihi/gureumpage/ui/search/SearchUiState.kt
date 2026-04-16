package com.hihihihi.gureumpage.ui.search

import com.hihihihi.domain.model.SearchBook

data class SearchUiState(
    val query: String = "",
    val searchResults: List<SearchBook> = emptyList(),
    val visibleCount: Int = 0,
    val page: Int = 1,
    val pageSize: Int = 10,
    val isSearching: Boolean = false,
    val isPaging: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasMore: Boolean = true,
    val isAddingBook: Boolean = false,
    val addBookMessage: String? = null,
    val isAddBookSuccess: Boolean = false
)