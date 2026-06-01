package com.hihihihi.presentation.ui.library

import androidx.compose.runtime.Immutable
import com.hihihihi.presentation.ui.model.UserBookUiModel

@Immutable
sealed interface LibraryUiState {
    @Immutable
    data object Loading : LibraryUiState

    @Immutable
    data class Content(
        val books: List<UserBookUiModel> = emptyList(),
    ) : LibraryUiState

    @Immutable
    data class Error(
        val message: String,
        val previous: Content? = null,
    ) : LibraryUiState
}

internal fun LibraryUiState.contentOrDefault(): LibraryUiState.Content = when (this) {
    is LibraryUiState.Content -> this
    is LibraryUiState.Error -> previous ?: LibraryUiState.Content()
    LibraryUiState.Loading -> LibraryUiState.Content()
}
