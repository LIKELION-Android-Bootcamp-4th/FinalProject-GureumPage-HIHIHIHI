package com.hihihihi.presentation.ui.bookdetail

import androidx.compose.runtime.Immutable
import com.hihihihi.presentation.ui.model.HistoryUiModel
import com.hihihihi.presentation.ui.model.QuoteUiModel
import com.hihihihi.presentation.ui.model.UserBookUiModel

@Immutable
sealed interface BookDetailUiState {
    @Immutable
    data object Loading : BookDetailUiState

    @Immutable
    data class Content(
        val userBook: UserBookUiModel? = null,
        val quotes: List<QuoteUiModel> = emptyList(),
        val histories: List<HistoryUiModel> = emptyList(),
        val dialogState: BookDetailDialogState = BookDetailDialogState.None,
    ) : BookDetailUiState

    @Immutable
    data class Error(
        val message: String,
        val previous: Content? = null,
    ) : BookDetailUiState
}

internal fun BookDetailUiState.contentOrDefault(): BookDetailUiState.Content = when (this) {
    is BookDetailUiState.Content -> this
    is BookDetailUiState.Error -> previous ?: BookDetailUiState.Content()
    BookDetailUiState.Loading -> BookDetailUiState.Content()
}
