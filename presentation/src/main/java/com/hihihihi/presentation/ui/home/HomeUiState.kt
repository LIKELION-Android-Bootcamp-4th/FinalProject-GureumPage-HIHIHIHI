package com.hihihihi.presentation.ui.home

import androidx.compose.runtime.Immutable
import com.hihihihi.presentation.ui.model.HomeUiModel

@Immutable
data class HomeUiState(
    val isLoading: Boolean = false,
    val homeUiModel: HomeUiModel? = null,
    val errorMessage: String? = null,
)
