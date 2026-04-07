package com.hihihihi.presentation.ui.library

import com.hihihihi.presentation.ui.model.UserBookUiModel

data class LibraryUiState(
    val isLoading: Boolean = false,
    val books: List<UserBookUiModel> = emptyList(),
    val errorMessage: String? = null,
)
