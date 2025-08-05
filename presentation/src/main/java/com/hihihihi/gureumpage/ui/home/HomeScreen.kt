package com.hihihihi.gureumpage.ui.home

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.graphicsLayer
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieClipSpec
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieAnimatable
import com.airbnb.lottie.compose.rememberLottieComposition
import com.hihihihi.domain.model.UserBook
import com.hihihihi.gureumpage.R
import com.hihihihi.gureumpage.designsystem.components.GureumCard
import com.hihihihi.gureumpage.designsystem.components.TitleText
import com.hihihihi.gureumpage.designsystem.theme.GureumPageTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTypography
import com.hihihihi.gureumpage.navigation.NavigationRoute
import com.hihihihi.gureumpage.ui.home.components.CurrentReadingBookSection
import com.hihihihi.gureumpage.ui.home.components.EmptyView
import com.hihihihi.gureumpage.ui.home.components.ErrorView
import com.hihihihi.gureumpage.ui.home.components.LoadingView
import com.hihihihi.gureumpage.ui.home.components.SearchBarWithBackground
import com.hihihihi.gureumpage.ui.home.mock.mockUserBooks
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.text.style.TextOverflow
import com.hihihihi.gureumpage.ui.home.components.RandomQuoteSection


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

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            // 원래 이 바깥에 이미지를 뒀더니 길어짐에 따라 뒷배경 이미지가 이상하게 작동돼서.. 일단 잔디색 고정값
            .background(color = Color(0xFF276040)),
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
            RandomQuoteSection()
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

