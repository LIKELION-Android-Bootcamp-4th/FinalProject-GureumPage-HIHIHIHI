package com.hihihihi.presentation.ui.quotes

import com.hihihihi.presentation.ui.model.QuoteUiModel

data class QuotesUiState(
    val isLoading: Boolean = false,
    val quotes: List<QuoteUiModel> = emptyList(),
    val errorMessage: String? = null,
    val selectedQuote: QuoteUiModel? = null,
)
