package com.hihihihi.presentation.ui.withdraw

import androidx.compose.runtime.Immutable

@Immutable
sealed interface WithdrawUiState {
    @Immutable
    data object Loading : WithdrawUiState

    @Immutable
    data class Content(
        val isLoading: Boolean = false,
        val loadingMessage: String = "",
    ) : WithdrawUiState

    @Immutable
    data class Error(
        val message: String,
        val previous: Content? = null,
    ) : WithdrawUiState
}

internal fun WithdrawUiState.contentOrDefault(): WithdrawUiState.Content = when (this) {
    is WithdrawUiState.Content -> this
    is WithdrawUiState.Error -> previous ?: WithdrawUiState.Content()
    WithdrawUiState.Loading -> WithdrawUiState.Content(isLoading = true)
}
