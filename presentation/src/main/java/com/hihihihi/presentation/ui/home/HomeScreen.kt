package com.hihihihi.presentation.ui.home

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hihihihi.domain.model.NotificationSettings
import com.hihihihi.presentation.designsystem.theme.GureumPageTheme
import com.hihihihi.presentation.ui.home.components.CurrentReadingBookSection
import com.hihihihi.presentation.ui.home.components.ErrorView
import com.hihihihi.presentation.ui.home.components.LoadingView
import com.hihihihi.presentation.ui.home.components.RandomQuoteSection
import com.hihihihi.presentation.ui.home.components.ReadingGoalSection
import com.hihihihi.presentation.ui.home.components.SearchBarWithBackground
import com.hihihihi.presentation.ui.home.mock.mockHomeUiModel
import com.hihihihi.presentation.ui.model.HomeUiModel

@Composable
fun HomeScreen(
    onNavigateToBookDetail: (String) -> Unit,
    onNavigateToSearch: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        HomeUiState.Loading -> LoadingView()
        is HomeUiState.Error -> ErrorView(message = "홈 화면 데이터를 가져오는데 실패했어요")
        is HomeUiState.Content -> {
            val homeUiModel = state.homeUiModel
            if (homeUiModel == null) {
                LoadingView()
            } else {
                Column {
                    HomeScreenContent(
                        homeUiModel = homeUiModel,
                        notificationSettings = state.notificationSettings,
                        onBookClick = onNavigateToBookDetail,
                        onSearchBarClick = onNavigateToSearch,
                        onChangeDailyGoalTime = { viewModel.changeDailyGoalTime(it) },
                    )
                }
            }
        }
    }
}

@Composable
fun HomeScreenContent(
    homeUiModel: HomeUiModel,
    notificationSettings: NotificationSettings = NotificationSettings(),
    onBookClick: (String) -> Unit,
    onChangeDailyGoalTime: (Int) -> Unit,
    onSearchBarClick: () -> Unit,
) {
    val scrollState = rememberLazyListState()
    val goalSeconds by rememberUpdatedState(newValue = homeUiModel.dailyGoalTime)
    val totalReadSeconds by rememberUpdatedState(newValue = homeUiModel.todayReadTime)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = scrollState,
    ) {
        item {
            SearchBarWithBackground(
                nickname = homeUiModel.nickname,
                appellation = homeUiModel.appellation,
                onSearchBarClick = onSearchBarClick,
            )
        }
        item {
            CurrentReadingBookSection(
                books = homeUiModel.userBooks,
                onBookClick = { onBookClick(it) },
                onAddBookClick = onSearchBarClick,
            )
        }
        item {
            RandomQuoteSection(quotes = homeUiModel.quotes)
        }
        item {
            ReadingGoalSection(
                totalReadSeconds,
                goalSeconds,
                notificationSettings,
                onGoalChange = onChangeDailyGoalTime,
            )
        }
    }
}

@Preview(name = "DarkMode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "LightMode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun HomePreview() {
    GureumPageTheme {
        HomeScreenContent(
            homeUiModel = mockHomeUiModel,
            onBookClick = {},
            onChangeDailyGoalTime = {},
            onSearchBarClick = {},
        )
    }
}
