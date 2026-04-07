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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hihihihi.domain.model.Quote
import com.hihihihi.domain.model.User
import com.hihihihi.domain.model.UserBook
import com.hihihihi.presentation.designsystem.theme.GureumPageTheme
import com.hihihihi.presentation.ui.home.components.CurrentReadingBookSection
import com.hihihihi.presentation.ui.home.components.ErrorView
import com.hihihihi.presentation.ui.home.components.LoadingView
import com.hihihihi.presentation.ui.home.components.RandomQuoteSection
import com.hihihihi.presentation.ui.home.components.ReadingGoalSection
import com.hihihihi.presentation.ui.home.components.SearchBarWithBackground
import com.hihihihi.presentation.ui.home.mock.dummyQuotes
import com.hihihihi.presentation.ui.home.mock.mockUser
import com.hihihihi.presentation.ui.home.mock.mockUserBooks

@Composable
fun HomeScreen(
    onNavigateToBookDetail: (String) -> Unit,
    onNavigateToSearch: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    when {
        uiState.value.isLoading -> {
            LoadingView()
        }

        uiState.value.errorMessage != null -> {
            ErrorView(message = "홈 화면 데이터를 가져오는데 실패했어요")
        }

        uiState.value.homeData != null -> {
            val homeData = uiState.value.homeData!!

            Column {
                HomeScreenContent(
                    user = homeData.user,
                    books = homeData.userBooks,
                    quotes = homeData.quotes,
                    todayReadTime = homeData.todayReadTime,
                    dailyGoalTime = homeData.user.dailyGoalTime,
                    onBookClick = onNavigateToBookDetail,
                    onSearchBarClick = onNavigateToSearch,
                    onChangeDailyGoalTime = { viewModel.changeDailyGoalTime(it) }
                )
            }
        }
    }
}

@Composable
fun HomeScreenContent(
    user: User,
    books: List<UserBook>,
    quotes: List<Quote>,
    todayReadTime: Int,
    dailyGoalTime: Int,
    onBookClick: (String) -> Unit,
    onChangeDailyGoalTime: (Int) -> Unit,
    onSearchBarClick: () -> Unit
) {
    val scrollState = rememberLazyListState()

    val goalSeconds by rememberUpdatedState(newValue = dailyGoalTime)
    val totalReadSeconds by rememberUpdatedState(newValue = todayReadTime)


    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        state = scrollState,
    ) {
        item {
            SearchBarWithBackground(
                user = user,
                onSearchBarClick,
            )
        }

        item {
            CurrentReadingBookSection(
                books = books,
                onBookClick = { onBookClick(it) },
                onAddBookClick = onSearchBarClick
            )
        }

        item {
            RandomQuoteSection(
                quotes = quotes
            )
        }

        item {
            ReadingGoalSection(
                totalReadSeconds,
                goalSeconds,
                onGoalChange = onChangeDailyGoalTime
            )
        }
    }
}

@Preview(name = "DarkMode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "LightMode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun HomePreview() {
    GureumPageTheme {
        HomeScreenContent(mockUser, mockUserBooks, dummyQuotes, 200, 300, onBookClick = {}, {}, {})
    }
}

