package com.hihihihi.presentation.ui.login

data class LoginUiState(
    val lastProvider: String? = "",
    val isLoading: Boolean = false,
    val loadingMessage: String? = null,
    val errorMessage: String? = null
)
