package com.hihihihi.gureumpage.ui.home

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import com.hihihihi.domain.model.UserBook
import com.hihihihi.gureumpage.designsystem.theme.GureumPageTheme
import com.hihihihi.gureumpage.navigation.NavigationRoute
import com.hihihihi.gureumpage.ui.home.components.CurrentReadingBookSection
import com.hihihihi.gureumpage.ui.home.components.ErrorView
import com.hihihihi.gureumpage.ui.home.components.LoadingView
import com.hihihihi.gureumpage.ui.home.components.SearchBarWithBackground
import com.hihihihi.gureumpage.ui.home.mock.mockUserBooks
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.hihihihi.domain.model.Quote
import com.hihihihi.gureumpage.ui.home.components.RandomQuoteSection
import com.hihihihi.gureumpage.ui.home.components.ReadingGoalSection
import com.hihihihi.gureumpage.ui.home.mock.dummyQuotes


@Composable
fun HomeScreen(
    // Hilt로 ViewModel을 주입받음. DI를 통해 뷰모델의 생명주기를 컴포즈에 맞게 관리
    navController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel(),

    ) {
    // viewModel에서 선언한 uiState Flow를 Compose에서 관찰 (State로 변환).
    // 상태가 변경되면 recomposition 발생
    val uiState = viewModel.uiState.collectAsState()


    // ui
    when {
        uiState.value.isLoading -> {
            LoadingView() // 데이터 로딩 중일 때 표시될 뷰. 분리된 Composable 사용
        }

        uiState.value.errorMessage != null -> {
            ErrorView(message = uiState.value.errorMessage!!) // 에러 발생 시 표시될 뷰
        }

        uiState.value.homeData != null -> {
            val homeData = uiState.value.homeData!! // null 아님 확정

            Column {
                HomeScreenContent(
                    books = homeData.userBooks,
                    quotes = homeData.quotes,
                    todayReadTime = homeData.todayReadTime,
                    dailyGoalTime = homeData.user.dailyGoalTime,
                    onBookClick = {
                        navController.navigate(NavigationRoute.BookDetail.createRoute(it))
                    },
                    onSearchBarClick = {
                        navController.navigate(NavigationRoute.Search.route)
                    }
                )
            }
        }
    }
}

@Composable
fun HomeScreenContent(
    books: List<UserBook>,
    quotes: List<Quote>,
    todayReadTime: Int,
    dailyGoalTime: Int,
    onBookClick: (String) -> Unit,
    onSearchBarClick: () -> Unit
) {
    val scrollState = rememberLazyListState()

    var goalSeconds by remember { mutableStateOf(3720) }
    val totalReadSeconds = 3802 // 실제 읽은 시간 데이터로 교체 필요

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            // 원래 이 바깥에 이미지를 뒀더니 길어짐에 따라 뒷배경 이미지가 이상하게 작동돼서.. 일단 잔디색 고정값
            .background(color = Color(0xFF276040)),
        state = scrollState,
    ) {
        item {
            SearchBarWithBackground(
                onSearchBarClick
            )
        }

        item {
            CurrentReadingBookSection(
                books = books,
                onBookClick = { onBookClick(it) }
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
                onGoalChange = { newGoal ->
                    goalSeconds = newGoal
                }
            )
        }
    }
}

@Preview(name = "DarkMode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "LightMode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun HomePreview() {
    GureumPageTheme {
        HomeScreenContent(mockUserBooks, dummyQuotes, 200,300,onBookClick = {}, {})
    }
}

