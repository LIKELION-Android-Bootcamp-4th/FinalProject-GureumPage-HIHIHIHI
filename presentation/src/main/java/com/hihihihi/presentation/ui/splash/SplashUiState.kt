package com.hihihihi.presentation.ui.splash

import androidx.compose.runtime.Immutable

@Immutable
sealed interface SplashUiState {
    @Immutable
    data object Loading : SplashUiState

    @Immutable
    data class Content(
        val navTarget: SplashViewModel.NavTarget = SplashViewModel.NavTarget.Loading,
        val loadingMessage: String = "구름이를 깨우는 중...",
        val progress: Float = 0f,
        val isLoading: Boolean = true,
        val permissionAsked: Boolean = false,
        val permissionHandled: Boolean = false,
        val schedulersSetUp: Boolean = false,
    ) : SplashUiState

    @Immutable
    data class Error(
        val message: String,
        val previous: Content? = null,
    ) : SplashUiState
}

internal fun SplashUiState.contentOrDefault(): SplashUiState.Content = when (this) {
    is SplashUiState.Content -> this
    is SplashUiState.Error -> previous?.copy(loadingMessage = message, isLoading = false, progress = 1f)
        ?: SplashUiState.Content(
            navTarget = SplashViewModel.NavTarget.NoNetwork,
            loadingMessage = message,
            isLoading = false,
            progress = 1f,
        )
    SplashUiState.Loading -> SplashUiState.Content()
}
