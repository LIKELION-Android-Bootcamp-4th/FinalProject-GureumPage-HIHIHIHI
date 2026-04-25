package com.hihihihi.presentation.ui.onboarding

import androidx.compose.runtime.Immutable
import com.hihihihi.domain.model.GureumThemeType

@Immutable
data class OnBoardingUiState(
    val nickname: String = "",
    val selectedPurposes: List<String> = emptyList(),
    val currentInnerPage: Int = 0,
    val featurePageCount: Int = 0,
    val theme: GureumThemeType? = null,
)
