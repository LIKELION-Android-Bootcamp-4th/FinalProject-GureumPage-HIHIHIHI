package com.hihihihi.gureumpage.ui.onboarding.pages

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hihihihi.domain.model.GureumThemeType
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTypography
import com.hihihihi.gureumpage.ui.onboarding.OnBoardingViewModel
import com.hihihihi.gureumpage.ui.onboarding.components.OnBoardingMainContents
import com.hihihihi.gureumpage.ui.onboarding.components.OnboardingThemeCard

@Composable
fun ThemePage(viewModel: OnBoardingViewModel) {
    val selectedTheme = viewModel.theme
    OnBoardingMainContents(
        titleText = "선호하는 테마를 선택해주세요",
        subTitleText = "언제든 설정에서 변경할 수 있어요",
        showGureum = false
    ) {
        Spacer(Modifier.height(6.dp))
        Text(
            text = "독서에 집중할 수 있는 테마를 골라보세요",
            style = GureumTypography.bodyMedium,
            color = GureumTheme.colors.gray400
        )
        Spacer(Modifier.height(24.dp))
        OnboardingThemeCard(
            isDarkTheme = true,
            selected = selectedTheme == GureumThemeType.DARK,
            onSelected = { viewModel.selectTheme(GureumThemeType.DARK) }
        )
        Spacer(Modifier.height(24.dp))
        OnboardingThemeCard(
            isDarkTheme = false,
            selected = selectedTheme == GureumThemeType.LIGHT,
            onSelected = { viewModel.selectTheme(GureumThemeType.LIGHT) }
        )
    }
}
