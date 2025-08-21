package com.hihihihi.gureumpage.ui.search

import com.hihihihi.domain.model.SearchBook

data class SearchUiState(
    val searchResults: List<SearchBook> = emptyList(),
    val isSearching: Boolean = false,
    val isAddingBook: Boolean = false,
    val addBookMessage: String? = null,
    val isAddBookSuccess: Boolean = false
)