package com.hihihihi.gureumpage.ui.onboarding.pages

import androidx.compose.runtime.Composable
import com.hihihihi.gureumpage.ui.onboarding.components.OnBoardingMainContents

@Composable
fun WelcomePage() {
    OnBoardingMainContents(
        titleText = "구름한장에\n오신 것을 환영합니다",
        subTitleText = "독서 기록부터 통계까지,\n당신만의 독서 여정을\n함께 만들어가요"
    ) { }
}