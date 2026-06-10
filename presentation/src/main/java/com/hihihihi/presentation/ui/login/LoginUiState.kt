package com.hihihihi.presentation.ui.login

import androidx.compose.runtime.Immutable

@Immutable
sealed interface LoginUiState {
    @Immutable
    data object Loading : LoginUiState

    @Immutable
    data class Content(
        val lastProvider: String? = "",
        val isLoading: Boolean = false,
        val loadingMessage: String? = null,
    ) : LoginUiState

    @Immutable
    data class Error(
        val message: String,
        val previous: Content? = null,
    ) : LoginUiState
}

internal fun LoginUiState.contentOrDefault(): LoginUiState.Content = when (this) {
    is LoginUiState.Content -> this
    is LoginUiState.Error -> previous ?: LoginUiState.Content()
    LoginUiState.Loading -> LoginUiState.Content(isLoading = true)
}
