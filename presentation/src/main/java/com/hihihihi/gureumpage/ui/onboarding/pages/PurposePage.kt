package com.hihihihi.gureumpage.ui.onboarding.pages

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.hihihihi.gureumpage.R
import com.hihihihi.gureumpage.designsystem.theme.GureumPageTheme
import com.hihihihi.gureumpage.ui.onboarding.components.OnBoardingMainContents
import com.hihihihi.gureumpage.ui.onboarding.components.OnboardingBottom
import com.hihihihi.gureumpage.ui.onboarding.components.OnboardingPurposeCard
import com.hihihihi.gureumpage.ui.onboarding.components.OnboardingScaffold
import com.hihihihi.gureumpage.ui.onboarding.components.OnboardingTopContents
import com.hihihihi.gureumpage.ui.onboarding.model.OnboardingPurposeContents

@Composable
fun PurposePage(navController: NavHostController) {
    OnboardingScaffold(
        topContent = {
            OnboardingTopContents(
                navController = navController,
                progress = 0.4f
            )
        },
        mainContent = {
            OnBoardingMainContents(
                titleText = "구름한장을 사용하는\n목적을 알려주세요",
                subTitleText = "한가지 이상 선택해주세요",
                showGureum = false
            ) {
                Spacer(Modifier.height(20.dp))

                Column(
                    modifier = Modifier.padding(horizontal = 20.dp)
                ) {
                    Column {
                        purposeContents.forEach { content ->
                            OnboardingPurposeCard(cardItem = content)
                            Spacer(Modifier.height(10.dp))
                        }
                    }
                }
            }
        },
        bottomContent = {
            OnboardingBottom("다음 단계")
        }
    )
}

// TODO: 이미지 변경하기
private val purposeContents = listOf(
    OnboardingPurposeContents(R.drawable.ic_cloud_reading, "독서 기록", "읽은 책을 기록하고 관리해요"),
    OnboardingPurposeContents(R.drawable.ic_cloud_reading, "독서 습관", "꾸준한 독서 습관을 만들고 싶어요"),
    OnboardingPurposeContents(R.drawable.ic_cloud_reading, "목표 달성", "독서 목표를 설정하고 달성하고 싶어요"),
    OnboardingPurposeContents(R.drawable.ic_cloud_reading, "독서 분석", "독서 패턴을 분석하고 통계를 보고 싶어요"),
    OnboardingPurposeContents(R.drawable.ic_cloud_reading, "필사 & 메모", "인상 깊은 문장을 기록하고 정리하고 싶어요"),
    OnboardingPurposeContents(R.drawable.ic_cloud_reading, "독서 즐기기", "단순히 독서를 더 즐겁게 하고 싶어요"),
)

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PurposePagePreview() {
    GureumPageTheme {
        PurposePage(rememberNavController())
    }
}