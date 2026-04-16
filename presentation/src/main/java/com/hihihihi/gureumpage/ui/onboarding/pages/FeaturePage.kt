package com.hihihihi.gureumpage.ui.onboarding.pages

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hihihihi.gureumpage.R
import com.hihihihi.gureumpage.designsystem.components.Medi14Text
import com.hihihihi.gureumpage.designsystem.theme.GureumPageTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTypography
import com.hihihihi.gureumpage.ui.onboarding.OnBoardingViewModel
import com.hihihihi.gureumpage.ui.onboarding.model.OnboardingFeature
import kotlinx.coroutines.flow.distinctUntilChanged

@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun FeaturePage(
    viewModel: OnBoardingViewModel
) {
    val pagerState = rememberPagerState { featurePages.size }

    LaunchedEffect(pagerState.currentPage) {
        snapshotFlow { pagerState.currentPage to pagerState.pageCount }
            .distinctUntilChanged()
            .collect { (currentPage, pageCount) ->
                viewModel.featurePageChanged(currentPage, pageCount)
            }
    }

    BoxWithConstraints(Modifier.fillMaxSize()) {
        // 바텀 시스템 내비 영역 인셋
        val bottomInset = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()
        val indicatorPadding =  maxHeight * 0.26f - bottomInset

        // 오버 스크롤 모션 삭제
        CompositionLocalProvider(LocalOverscrollConfiguration provides null) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { pageIndex ->
                val page = featurePages[pageIndex]

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(page.gureumImage),
                        contentDescription = "OnboardingGureumImage",
                        modifier = Modifier.size(120.dp)
                    )
                    Spacer(Modifier.height(14.dp))
                    Text(
                        text = page.title,
                        style = GureumTypography.displaySmall,
                        color = GureumTheme.colors.gray900,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = page.subTitle,
                        color = GureumTheme.colors.gray500,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )
                    Spacer(Modifier.height(24.dp))
                    Column {
                        page.details.forEach { detail ->
                            DotWithText(detail)
                            Spacer(Modifier.height(4.dp))
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                }
            }
        }
        PagerIndicator(
            state = pagerState, modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = indicatorPadding)
        )
    }
}

@Composable
private fun DotWithText(detail: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(GureumTheme.colors.primary)
        )
        Spacer(Modifier.width(8.dp))
        Medi14Text(
            text = detail,
            color = GureumTheme.colors.gray400
        )
    }
}

@Composable
private fun PagerIndicator(
    state: PagerState,
    modifier: Modifier = Modifier,
) {
    val colors = GureumTheme.colors
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(state.pageCount) { index ->
            val currentPage = state.currentPage == index
            val color = if (currentPage) colors.primary else colors.gray800
            Box(
                modifier = Modifier
                    .size(if (currentPage) 10.dp else 8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            if (index < state.pageCount - 1) {
                Spacer(Modifier.width(8.dp))
            }
        }
    }
}

private val featurePages = listOf(
    OnboardingFeature(
        R.drawable.ic_cloud_library,
        "나만의 디지털 서재",
        "읽은 책, 읽는 중, 완독한 책을\n체계적으로 관리해요",
        listOf("책 상태별 분류 관리", "독서 진행률 추적", "책 정보와 내 필사 보기")
    ),
    OnboardingFeature(
        R.drawable.ic_cloud_timer,
        "독서 스톱워치",
        "독서 시간을 측정하고\n꾸준한 독서 습관을 만들어보세요",
        listOf("스톱워치로 독서 시간 측정", "일일 목표 설정", "누적 통계 제공")
    ),
    OnboardingFeature(
        R.drawable.ic_cloud_mindmap,
        "필사 & 마인드맵",
        "인상 깊은 문장을 기록하거나\n인물, 정보의 관계를 마인드 맵으로 만들어요",
        listOf("책 속 문장 필사", "자유로운 마인드맵 생성", "인물 및 줄거리 관계도")
    ),
    OnboardingFeature(
        R.drawable.ic_cloud_statistics,
        "독서 통계 & 분석",
        "내 독서 패턴을 분석하고\n더 나은 독서 계획을 세워봐요",
        listOf("장르별 선호도 분석", "시간대별 독서 패턴", "월별/주간/연간 독서량 추이")
    ),
)

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun FeaturePagePreview() {
    GureumPageTheme {
//        FeaturePage()
    }
}