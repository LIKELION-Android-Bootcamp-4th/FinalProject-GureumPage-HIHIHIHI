package com.hihihihi.gureumpage.ui.withdraw

data class WithdrawUiState(
    val isLoading: Boolean = false,
    val loadingMessage: String = "",
    val errorMessage: String? = null
)