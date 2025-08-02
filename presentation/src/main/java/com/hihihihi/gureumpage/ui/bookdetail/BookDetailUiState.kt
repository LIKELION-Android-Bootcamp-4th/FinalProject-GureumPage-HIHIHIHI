package com.hihihihi.gureumpage.ui.bookdetail

import com.hihihihi.domain.model.Book
import com.hihihihi.domain.model.Quote
import com.hihihihi.domain.model.UserBook

data class BookDetailUiState(
    val book: Book?,
    val userBook: UserBook?,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val quotes: List<Quote> = emptyList(),
    val addQuoteState: AddQuoteState = AddQuoteState()
)

data class AddQuoteState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val message: String? = null
)