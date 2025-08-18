package com.hihihihi.gureumpage.ui.onboarding.pages

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.hihihihi.gureumpage.R
import com.hihihihi.gureumpage.designsystem.theme.GureumPageTheme
import com.hihihihi.gureumpage.ui.onboarding.OnBoardingViewModel
import com.hihihihi.gureumpage.ui.onboarding.components.OnBoardingMainContents

@Composable
fun FinishPage(viewModel: OnBoardingViewModel) {
    val nickname = viewModel.nickname
    OnBoardingMainContents(
        titleText = "설정 완료!",
        subTitleText = "${nickname}님의\n구름한장 여정이 시작됩니다!",
        gureumRes = R.drawable.ic_cloud_complete
    ) {}
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun FinishPagePreview() {
    GureumPageTheme {
//        FinishPage(OnBoardingViewModel())
    }
}