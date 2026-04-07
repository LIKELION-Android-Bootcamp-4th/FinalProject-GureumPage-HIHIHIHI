package com.hihihihi.presentation.ui.bookdetail

import com.hihihihi.presentation.ui.model.HistoryUiModel
import com.hihihihi.presentation.ui.model.QuoteUiModel
import com.hihihihi.presentation.ui.model.UserBookUiModel

data class BookDetailUiState(
    val userBook: UserBookUiModel? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val quotes: List<QuoteUiModel> = emptyList(),
    val histories: List<HistoryUiModel> = emptyList(),
    val addQuoteState: AddQuoteState = AddQuoteState(),
    val dialogState: BookDetailDialogState = BookDetailDialogState.None,
)

data class AddQuoteState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val message: String? = null,
)
