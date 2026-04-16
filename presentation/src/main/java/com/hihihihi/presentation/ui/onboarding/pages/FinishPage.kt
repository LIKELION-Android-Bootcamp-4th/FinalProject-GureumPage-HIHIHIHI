package com.hihihihi.presentation.ui.onboarding.pages

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
import com.hihihihi.presentation.R
import com.hihihihi.presentation.designsystem.theme.GureumPageTheme
import com.hihihihi.presentation.ui.onboarding.components.OnBoardingMainContents

@Composable
fun FinishPage() {
    val confettiComposition by rememberLottieComposition(
        LottieCompositionSpec.Asset("confetti.json"),
    )
    val progress by animateLottieCompositionAsState(
        composition = confettiComposition,
        iterations = 1,
        isPlaying = true,
    )

    Box(modifier = Modifier.fillMaxSize()) {
        OnBoardingMainContents(
            titleText = "설정 완료!",
            subTitleText = "구름한장 여정이 시작됩니다!",
            gureumRes = R.drawable.ic_cloud_complete,
        ) {}

        if (progress < 1f) {
            LottieAnimation(
                composition = confettiComposition,
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter),
                contentScale = ContentScale.FillWidth,
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun FinishPagePreview() {
    GureumPageTheme {
        FinishPage()
    }
}
