package com.hihihihi.presentation.ui.home

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.hihihihi.domain.model.Quote
import com.hihihihi.domain.model.User
import com.hihihihi.domain.model.UserBook
import com.hihihihi.presentation.designsystem.theme.GureumPageTheme
import com.hihihihi.presentation.navigation.NavigationRoute
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
    // Hilt로 ViewModel을 주입받음. DI를 통해 뷰모델의 생명주기를 컴포즈에 맞게 관리
    navController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    // viewModel에서 선언한 uiState Flow를 Compose에서 관찰 (State로 변환).
    // 상태가 변경되면 recomposition 발생
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    // ui
    when {
        uiState.value.isLoading -> {
            LoadingView() // 데이터 로딩 중일 때 표시될 뷰. 분리된 Composable 사용
        }

        uiState.value.errorMessage != null -> {
            ErrorView(message = "홈 화면 데이터를 가져오는데 실패했어요") // 에러 발생 시 표시될 뷰
        }

        uiState.value.homeData != null -> {
            val homeData = uiState.value.homeData!! // null 아님 확정

            Column {
                HomeScreenContent(
                    user = homeData.user,
                    books = homeData.userBooks,
                    quotes = homeData.quotes,
                    todayReadTime = homeData.todayReadTime,
                    dailyGoalTime = homeData.user.dailyGoalTime,
                    onBookClick = {
                        navController.navigate(NavigationRoute.BookDetail.createRoute(it))
                    },
                    onSearchBarClick = {
                        navController.navigate(NavigationRoute.Search.route)
                    },
                    onChangeDailyGoalTime = {
                        viewModel.changeDailyGoalTime(it)
                    }
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

