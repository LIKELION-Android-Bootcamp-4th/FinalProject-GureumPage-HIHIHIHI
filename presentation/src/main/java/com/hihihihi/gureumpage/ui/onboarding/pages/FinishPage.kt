package com.hihihihi.gureumpage.ui.onboarding.pages

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.hihihihi.gureumpage.R
import com.hihihihi.gureumpage.designsystem.theme.GureumPageTheme
import com.hihihihi.gureumpage.ui.onboarding.OnBoardingViewModel
import com.hihihihi.gureumpage.ui.onboarding.components.OnBoardingMainContents

@Composable
fun FinishPage(viewModel: OnBoardingViewModel) {
    val nickname = viewModel.nickname
    val confettiComposition by rememberLottieComposition(
        LottieCompositionSpec.Asset("confetti.json")
    )

    val progress by animateLottieCompositionAsState(
        composition = confettiComposition,
        iterations = 1, // 한 번만 재생
                isPlaying = true    // 자동 재생
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // 기존 콘텐츠
        OnBoardingMainContents(
            titleText = "설정 완료!",
            subTitleText = "${nickname}님의\n구름한장 여정이 시작됩니다!",
            gureumRes = R.drawable.ic_cloud_complete
        ) {}

        // Confetti 애니메이션 오버레이
        if (progress < 1f) {
            LottieAnimation(
                composition = confettiComposition,
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter), // 화면 위쪽
                contentScale = ContentScale.FillWidth
            )
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun FinishPagePreview() {
    GureumPageTheme {
//        FinishPage(OnBoardingViewModel())
    }
}