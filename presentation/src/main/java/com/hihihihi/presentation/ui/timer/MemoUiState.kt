package com.hihihihi.presentation.ui.timer

import androidx.compose.runtime.Immutable
import com.hihihihi.domain.model.Quote

@Immutable
sealed interface MemoUiState {
    @Immutable
    data object Loading : MemoUiState

    @Immutable
    data class Content(
        val items: List<Quote> = emptyList(),
        val isLoading: Boolean = false,
    ) : MemoUiState

    @Immutable
    data class Error(
        val message: String,
        val previous: Content? = null,
    ) : MemoUiState
}

internal fun MemoUiState.contentOrDefault(): MemoUiState.Content = when (this) {
    is MemoUiState.Content -> this
    is MemoUiState.Error -> previous ?: MemoUiState.Content()
    MemoUiState.Loading -> MemoUiState.Content(isLoading = true)
}
