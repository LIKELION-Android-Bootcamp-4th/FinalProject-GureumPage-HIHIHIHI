package com.hihihihi.gureumpage.ui.onboarding.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hihihihi.gureumpage.ui.onboarding.model.OnboardingStep

@Composable
fun OnboardingScaffold(
    pagerState: PagerState,
    topContent: @Composable (page: Int, step: OnboardingStep) -> Unit,
    mainContent: @Composable (page: Int, step: OnboardingStep) -> Unit,
    bottomContent: @Composable (page: Int, step: OnboardingStep) -> Unit,
    steps: List<OnboardingStep>,
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            val page = pagerState.currentPage
            topContent(page, steps[page])
        }

        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                mainContent(page, steps[page])
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            val page = pagerState.currentPage
            bottomContent(page, steps[page])
        }
    }
}