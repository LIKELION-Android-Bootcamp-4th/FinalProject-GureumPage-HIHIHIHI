package com.hihihihi.gureumpage.ui.onboarding.pages

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.hihihihi.gureumpage.designsystem.theme.GureumPageTheme
import com.hihihihi.gureumpage.ui.onboarding.components.OnBoardingMainContents

@Composable
fun FinishPage() {
    OnBoardingMainContents(
        titleText = "설정 완료!",
        subTitleText = "닉네임님의\n구름한장 여정이 시작됩니다!" // TODO 받은 닉네임으로 변경
    ) {}
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun FinishPagePreview() {
    GureumPageTheme {
        FinishPage()
    }
}