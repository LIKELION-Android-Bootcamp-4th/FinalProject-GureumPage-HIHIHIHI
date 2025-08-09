package com.hihihihi.gureumpage.ui.onboarding.pages

import android.content.res.Configuration
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.hihihihi.gureumpage.designsystem.theme.GureumPageTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTypography
import com.hihihihi.gureumpage.ui.onboarding.components.OnBoardingMainContents
import com.hihihihi.gureumpage.ui.onboarding.components.OnboardingBottom
import com.hihihihi.gureumpage.ui.onboarding.components.OnboardingScaffold
import com.hihihihi.gureumpage.ui.onboarding.components.OnboardingThemeCard

@Composable
fun ThemePage(navController: NavHostController) {
    val selected by remember { mutableStateOf(false) }

    OnboardingScaffold(
        topContent = {},
        mainContent = {
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
                OnboardingThemeCard(isDarkTheme = true, selected = !selected)
                Spacer(Modifier.height(24.dp))
                OnboardingThemeCard(isDarkTheme = false, selected = selected)
            }
        },
        bottomContent = {
            OnboardingBottom("구름한장 시작하기")
        },
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ThemePagePreview() {
    GureumPageTheme {
        ThemePage(rememberNavController())
    }
}