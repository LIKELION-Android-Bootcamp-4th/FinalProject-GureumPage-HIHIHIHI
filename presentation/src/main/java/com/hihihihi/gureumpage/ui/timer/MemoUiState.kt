package com.hihihihi.gureumpage.ui.timer

import com.hihihihi.domain.model.Quote

data class MemoUiState(
    val items: List<Quote> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)