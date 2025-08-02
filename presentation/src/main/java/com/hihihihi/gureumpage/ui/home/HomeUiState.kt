package com.hihihihi.gureumpage.ui.home

import com.hihihihi.domain.model.UserBook

data class HomeUiState(
    val isLoading: Boolean = false,
    val books: List<UserBook> = emptyList(),
    val errorMessage: String? = null,
    val isEmpty: Boolean = false
)
