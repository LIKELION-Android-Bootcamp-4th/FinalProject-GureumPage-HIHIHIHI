package com.hihihihi.gureumpage.ui.home

import com.hihihihi.domain.model.DomainUserBook

data class HomeUiState(
    val isLoading: Boolean = false,
    val books: List<DomainUserBook> = emptyList(),
    val errorMessage: String? = null,
    val isEmpty: Boolean = false
)
