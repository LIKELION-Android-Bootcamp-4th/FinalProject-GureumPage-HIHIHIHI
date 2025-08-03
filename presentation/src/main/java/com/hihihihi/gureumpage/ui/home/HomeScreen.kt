package com.hihihihi.gureumpage.ui.home

import android.content.res.Configuration
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.hihihihi.gureumpage.R
import com.hihihihi.gureumpage.designsystem.component.Floating
import com.hihihihi.gureumpage.designsystem.theme.GureumPageTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTypography
import com.hihihihi.gureumpage.navigation.NavigationRoute
import com.hihihihi.gureumpage.ui.home.components.EmptyView
import com.hihihihi.gureumpage.ui.home.components.ErrorView
import com.hihihihi.gureumpage.ui.home.components.LoadingView
import com.hihihihi.gureumpage.ui.home.components.SearchBarWithBackground
import com.hihihihi.gureumpage.ui.home.mock.mockUser

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
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = scrollState,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item {
                    SearchBarWithBackground()

                }

                item {
                    Text("Home Screen")

                }
                item {
                    Button(
                        onClick = {
                            navController.navigate(NavigationRoute.BookDetail.createRoute("123"))  // TODO 123은 임시 고정 ID 입니다. 추후 실제 ID 값 넘기기
                        }
                    ) {
                        Text("책 상세로 이동")
                    }
                }
                item {
                    uiState.value.books.forEach { book ->
                        Text(text = book.title)// 간단하게 책 제목만 출력. 추후 BookCard 형태로 개선
                    }
                }

            }
        }
    }
}


@Preview(name = "DarkMode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "LightMode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun HomePreview() {
    val fakeNavController = rememberNavController()

    GureumPageTheme {
        HomeScreen(fakeNavController)
    }
}