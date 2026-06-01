package com.hihihihi.presentation.ui.quotes

import androidx.compose.runtime.Immutable
import com.hihihihi.presentation.ui.model.QuoteUiModel

@Immutable
sealed interface QuotesUiState {
    @Immutable
    data object Loading : QuotesUiState

    @Immutable
    data class Content(
        val quotes: List<QuoteUiModel> = emptyList(),
        val selectedQuote: QuoteUiModel? = null,
    ) : QuotesUiState

    @Immutable
    data class Error(
        val message: String,
        val previous: Content? = null,
    ) : QuotesUiState
}

internal fun QuotesUiState.contentOrDefault(): QuotesUiState.Content = when (this) {
    is QuotesUiState.Content -> this
    is QuotesUiState.Error -> previous ?: QuotesUiState.Content()
    QuotesUiState.Loading -> QuotesUiState.Content()
}
