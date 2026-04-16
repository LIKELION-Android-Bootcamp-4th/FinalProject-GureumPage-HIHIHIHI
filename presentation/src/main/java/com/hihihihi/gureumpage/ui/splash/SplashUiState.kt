package com.hihihihi.gureumpage.ui.splash

data class SplashUiState (
    val navTarget: SplashViewModel.NavTarget = SplashViewModel.NavTarget.Loading,
    val loadingMessage: String = "구름이를 깨우는 중...",
    val progress: Float = 0f,
    val isLoading: Boolean = true
)

