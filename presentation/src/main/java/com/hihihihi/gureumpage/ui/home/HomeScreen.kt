package com.hihihihi.gureumpage.ui.home

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hihihihi.domain.model.UserBook
import com.hihihihi.gureumpage.R
import com.hihihihi.gureumpage.designsystem.theme.GureumPageTheme
import com.hihihihi.gureumpage.navigation.NavigationRoute
import com.hihihihi.gureumpage.ui.home.components.CurrentReadingBookSection
import com.hihihihi.gureumpage.ui.home.components.EmptyView
import com.hihihihi.gureumpage.ui.home.components.ErrorView
import com.hihihihi.gureumpage.ui.home.components.LoadingView
import com.hihihihi.gureumpage.ui.home.components.SearchBarWithBackground
import com.hihihihi.gureumpage.ui.home.mock.mockUserBooks

@Composable
fun HomeScreen(
    // Hilt로 ViewModel을 주입받음. DI를 통해 뷰모델의 생명주기를 컴포즈에 맞게 관리
    navController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel(),

    ) {
    // viewModel에서 선언한 uiState Flow를 Compose에서 관찰 (State로 변환).
    // 상태가 변경되면 recomposition 발생
    val uiState = viewModel.uiState.collectAsState()

    val scrollState = rememberLazyListState()

    // ui
    when {
        uiState.value.isLoading -> {
            LoadingView() // 데이터 로딩 중일 때 표시될 뷰. 분리된 Composable 사용
        }

        uiState.value.errorMessage != null -> {
            ErrorView(message = uiState.value.errorMessage!!) // 에러 발생 시 표시될 뷰
        }

        uiState.value.books.isEmpty() -> {
            EmptyView() // 데이터는 정상 응답됐지만 책 목록이 비어있을 때 표시
        }

        else -> {
            HomeScreenContent(
                books = uiState.value.books,
                onBookClick = {
                navController.navigate(NavigationRoute.BookDetail.createRoute(it))
            })
        }
    }
}

@Composable
fun HomeScreenContent(
    books: List<UserBook>,
    onBookClick: (String) -> Unit
) {
    val scrollState = rememberLazyListState()

    Box(modifier = Modifier.fillMaxSize()) {
        // 배경 (겹쳐지는 것까지 포함)
        Image(
            //TODO 이미지 테마에 맞춰 변경되어야 함 현재는 Light BG가 없음
            painter = painterResource(id = R.drawable.bg_home_dark),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp), // 배경이 겹쳐질 만큼만 높이
            contentScale = ContentScale.Crop
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = scrollState,
        ) {
            item {
                SearchBarWithBackground()

            }

            item {
                CurrentReadingBookSection(
                    books = books,
                    onBookClick = { onBookClick(it) }
                )
            }

            item {
                Text("Home Screen")

            }
            item {
                Button(
                    onClick = {
                    }
                ) {
                    Text("책 상세로 이동")
                }
            }
//        item {
//            uiState.value.books.forEach { book ->
//                Text(text = book.title)// 간단하게 책 제목만 출력. 추후 BookCard 형태로 개선
//            }
//        }
        }
    }
}



@Preview(name = "DarkMode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "LightMode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun HomePreview() {
    GureumPageTheme {
        HomeScreenContent(mockUserBooks, onBookClick = {})
    }
}


