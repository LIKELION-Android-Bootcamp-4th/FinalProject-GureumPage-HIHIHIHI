package com.hihihihi.presentation.ui.onboarding.pages

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hihihihi.domain.model.GureumThemeType
import com.hihihihi.presentation.designsystem.theme.GureumTheme
import com.hihihihi.presentation.designsystem.theme.GureumTypography
import com.hihihihi.presentation.ui.onboarding.components.OnBoardingMainContents
import com.hihihihi.presentation.ui.onboarding.components.OnboardingThemeCard

@Composable
fun ThemePage(
    selectedTheme: GureumThemeType?,
    onSelectTheme: (GureumThemeType) -> Unit,
) {
    OnBoardingMainContents(
        titleText = "선호하는 테마를 선택해주세요",
        subTitleText = "언제든 설정에서 변경할 수 있어요",
        showGureum = false,
    ) {
        Spacer(Modifier.height(6.dp))
        Text(
            text = "독서에 집중할 수 있는 테마를 골라보세요",
            style = GureumTypography.bodyMedium,
            color = GureumTheme.colors.gray400,
        )
        Spacer(Modifier.height(24.dp))
        OnboardingThemeCard(
            isDarkTheme = true,
            selected = selectedTheme == GureumThemeType.DARK,
            onSelected = { onSelectTheme(GureumThemeType.DARK) },
        )
        Spacer(Modifier.height(24.dp))
        OnboardingThemeCard(
            isDarkTheme = false,
            selected = selectedTheme == GureumThemeType.LIGHT,
            onSelected = { onSelectTheme(GureumThemeType.LIGHT) },
        )
    }
}
