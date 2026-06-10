package com.hihihihi.presentation.ui.search

import androidx.compose.runtime.Immutable
import com.hihihihi.domain.model.SearchBook

@Immutable
sealed interface SearchUiState {
    @Immutable
    data object Loading : SearchUiState

    @Immutable
    data class Content(
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
        val hasSearched: Boolean = false,
        val selectedBook: SearchBook? = null,
    ) : SearchUiState

    @Immutable
    data class Error(
        val message: String,
        val previous: Content? = null,
    ) : SearchUiState
}

internal fun SearchUiState.contentOrDefault(): SearchUiState.Content = when (this) {
    is SearchUiState.Content -> this
    is SearchUiState.Error -> previous ?: SearchUiState.Content()
    SearchUiState.Loading -> SearchUiState.Content()
}
