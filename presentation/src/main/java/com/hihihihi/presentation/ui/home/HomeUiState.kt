package com.hihihihi.presentation.ui.home

import com.hihihihi.presentation.ui.model.HomeUiModel

data class HomeUiState(
    val isLoading: Boolean = false,
    val homeUiModel: HomeUiModel? = null,
    val errorMessage: String? = null,
)
