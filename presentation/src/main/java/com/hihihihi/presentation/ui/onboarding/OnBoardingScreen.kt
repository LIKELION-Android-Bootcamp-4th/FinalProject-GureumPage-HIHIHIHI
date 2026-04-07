package com.hihihihi.presentation.ui.onboarding

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hihihihi.domain.model.GureumThemeType
import com.hihihihi.presentation.designsystem.theme.GureumPageTheme
import com.hihihihi.presentation.ui.onboarding.components.OnboardingBottomContents
import com.hihihihi.presentation.ui.onboarding.components.OnboardingScaffold
import com.hihihihi.presentation.ui.onboarding.components.OnboardingTopContents
import com.hihihihi.presentation.ui.onboarding.model.OnboardingStep
import com.hihihihi.presentation.ui.onboarding.pages.FeaturePage
import com.hihihihi.presentation.ui.onboarding.pages.FinishPage
import com.hihihihi.presentation.ui.onboarding.pages.NicknamePage
import com.hihihihi.presentation.ui.onboarding.pages.PurposePage
import com.hihihihi.presentation.ui.onboarding.pages.ThemePage
import com.hihihihi.presentation.ui.onboarding.pages.WelcomePage
import kotlinx.coroutines.launch

@Composable
fun OnBoardingScreen(
    onNavigateToHome: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: OnBoardingViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    GureumPageTheme(darkTheme = true) {
        OnboardingContents(
            steps = viewModel.steps,
            uiState = uiState,
            onNicknameChange = viewModel::updateNickname,
            onTogglePurpose = viewModel::togglePurpose,
            onFeaturePageChanged = viewModel::featurePageChanged,
            onSelectTheme = viewModel::selectTheme,
            isNextEnabled = viewModel::isNextEnabled,
            onNavigateBack = onNavigateBack,
            onSave = { viewModel.saveOnboardingComplete() },
            onFinish = onNavigateToHome,
        )
    }
}

@Composable
private fun OnboardingContents(
    steps: List<OnboardingStep>,
    uiState: OnBoardingUiState,
    onNicknameChange: (String) -> Unit,
    onTogglePurpose: (String) -> Unit,
    onFeaturePageChanged: (Int, Int) -> Unit,
    onSelectTheme: (GureumThemeType) -> Unit,
    isNextEnabled: (OnboardingStep) -> Boolean,
    onNavigateBack: () -> Unit,
    onSave: () -> Unit,
    onFinish: () -> Unit,
) {
    val pagerState = rememberPagerState { steps.size }
    val scope = rememberCoroutineScope()

    val currentStep = steps.getOrNull(pagerState.currentPage) ?: OnboardingStep.Welcome

    BackHandler {
        if (pagerState.currentPage > 0) {
            scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
        } else {
            onNavigateBack()
        }
    }

    OnboardingScaffold(
        pagerState = pagerState,
        topContent = { _, step ->
            if (step !is OnboardingStep.Welcome && step !is OnboardingStep.Finish) {
                OnboardingTopContents(
                    onBack = {
                        scope.launch {
                            if (pagerState.currentPage > 0) pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    },
                    progress = computeProgress(pagerState),
                )
            }
        },
        mainContent = { _, step ->
            when (step) {
                OnboardingStep.Welcome -> WelcomePage()
                OnboardingStep.Nickname -> NicknamePage(
                    nickname = uiState.nickname,
                    onNicknameChange = onNicknameChange,
                )

                OnboardingStep.Purpose -> PurposePage(
                    selectedPurposes = uiState.selectedPurposes,
                    onTogglePurpose = onTogglePurpose,
                )

                OnboardingStep.Feature -> FeaturePage(
                    onFeaturePageChanged = onFeaturePageChanged,
                )

                OnboardingStep.Theme -> ThemePage(
                    selectedTheme = uiState.theme,
                    onSelectTheme = onSelectTheme,
                )

                OnboardingStep.Finish -> FinishPage()
            }
        },
        bottomContent = { page, step ->
            val isLastPage = page == pagerState.pageCount - 1
            OnboardingBottomContents(
                buttonText = if (isLastPage) "시작하기" else "다음 단계",
                explanation = when (step) {
                    OnboardingStep.Welcome -> "설정은 언제든 변경할 수 있어요"
                    OnboardingStep.Feature -> "옆으로 밀어 구름한장의 기능을 확인해보세요!"
                    else -> ""
                },
                isNextEnabled = isNextEnabled(currentStep),
                onNext = {
                    scope.launch {
                        if (step == OnboardingStep.Theme) onSave()
                        if (step == OnboardingStep.Finish) onFinish()
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
