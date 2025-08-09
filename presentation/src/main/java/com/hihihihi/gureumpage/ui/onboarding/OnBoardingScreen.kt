package com.hihihihi.gureumpage.ui.onboarding

import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.hihihihi.gureumpage.designsystem.theme.GureumPageTheme
import com.hihihihi.gureumpage.navigation.NavigationRoute
import com.hihihihi.gureumpage.ui.onboarding.components.OnboardingBottomContents
import com.hihihihi.gureumpage.ui.onboarding.components.OnboardingScaffold
import com.hihihihi.gureumpage.ui.onboarding.components.OnboardingTopContents
import com.hihihihi.gureumpage.ui.onboarding.model.OnboardingStep
import com.hihihihi.gureumpage.ui.onboarding.pages.FeaturePage
import com.hihihihi.gureumpage.ui.onboarding.pages.FinishPage
import com.hihihihi.gureumpage.ui.onboarding.pages.NicknamePage
import com.hihihihi.gureumpage.ui.onboarding.pages.PurposePage
import com.hihihihi.gureumpage.ui.onboarding.pages.ThemePage
import com.hihihihi.gureumpage.ui.onboarding.pages.WelcomePage
import kotlinx.coroutines.launch

@Composable
fun OnBoardingScreen(
    navController: NavHostController,
    viewModel: OnBoardingViewModel = hiltViewModel(),
) {
    val steps by viewModel.steps.collectAsState()
    GureumPageTheme(darkTheme = true) {
        OnboardingContents(
            steps = steps,
            viewModel = viewModel,
            onFinish = {
                navController.navigate(NavigationRoute.Home.route)
            },
        )
    }
}

@Composable
private fun OnboardingContents(
    steps: List<OnboardingStep>,
    viewModel: OnBoardingViewModel,
    onFinish: () -> Unit
) {
    val pagerState = rememberPagerState { steps.size }
    val scope = rememberCoroutineScope()

    val currentStep = steps.getOrNull(pagerState.currentPage) ?: OnboardingStep.Welcome

    OnboardingScaffold(
        pagerState = pagerState,
        topContent = { page, step ->
            if (step !is OnboardingStep.Welcome && step !is OnboardingStep.Finish) {
                OnboardingTopContents(
                    onBack = {
                        scope.launch {
                            if (pagerState.currentPage > 0) pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    },
                    progress = computeProgress(pagerState)
                )
            }
        },
        mainContent = { page, step ->
            when (step) {
                OnboardingStep.Welcome -> WelcomePage()
                OnboardingStep.Nickname -> NicknamePage(viewModel = viewModel)
                OnboardingStep.Purpose -> PurposePage(viewModel = viewModel)
                OnboardingStep.Feature -> FeaturePage()
                OnboardingStep.Theme -> ThemePage(viewModel = viewModel)
                OnboardingStep.Finish -> FinishPage(viewModel = viewModel)
            }
        },
        bottomContent = { page, step ->
            val isLastPage = page == pagerState.pageCount - 1
            OnboardingBottomContents(
                buttonText = if (isLastPage) "시작하기" else "다음 단계",
                explanation = when (step) {
                    OnboardingStep.Welcome -> "설정은 언제든 변경할 수 있어요"
                    else -> ""
                },
                isNextEnabled = viewModel.isNextEnabled(currentStep),
                onNext = {
                    scope.launch {
                        if (isLastPage) onFinish()
                        else pagerState.animateScrollToPage(page + 1)
                    }
                },
            )
        },
        steps = steps,
    )
}

private fun computeProgress(pagerState: PagerState): Float {
    val position = pagerState.currentPage + pagerState.currentPageOffsetFraction
    return (position / (pagerState.pageCount - 1)).coerceIn(0f, 1f)
}
