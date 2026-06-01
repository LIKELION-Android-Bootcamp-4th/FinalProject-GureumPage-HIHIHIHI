package com.hihihihi.presentation.ui.onboarding

import androidx.compose.runtime.Immutable
import com.hihihihi.domain.model.GureumThemeType

@Immutable
sealed interface OnBoardingUiState {
    @Immutable
    data object Loading : OnBoardingUiState

    @Immutable
    data class Content(
        val nickname: String = "",
        val selectedPurposes: List<String> = emptyList(),
        val currentInnerPage: Int = 0,
        val featurePageCount: Int = 0,
        val theme: GureumThemeType? = null,
    ) : OnBoardingUiState

    @Immutable
    data class Error(
        val message: String,
        val previous: Content? = null,
    ) : OnBoardingUiState
}

internal fun OnBoardingUiState.contentOrDefault(): OnBoardingUiState.Content = when (this) {
    is OnBoardingUiState.Content -> this
    is OnBoardingUiState.Error -> previous ?: OnBoardingUiState.Content()
    OnBoardingUiState.Loading -> OnBoardingUiState.Content()
}
