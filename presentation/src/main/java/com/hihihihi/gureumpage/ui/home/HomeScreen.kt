package com.hihihihi.gureumpage.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.hihihihi.gureumpage.navigation.NavigationRoute
import com.hihihihi.gureumpage.ui.home.components.EmptyView
import com.hihihihi.gureumpage.ui.home.components.ErrorView
import com.hihihihi.gureumpage.ui.home.components.LoadingView

@Composable
fun HomeScreen(
    // Hilt로 ViewModel을 주입받음. DI를 통해 뷰모델의 생명주기를 컴포즈에 맞게 관리
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavHostController
) {
    // viewModel에서 선언한 uiState Flow를 Compose에서 관찰 (State로 변환).
    // 상태가 변경되면 recomposition 발생
    val uiState = viewModel.uiState.collectAsState()

    // 화면 진입 시, 호출
    // viewmodel에서 나중에 firebase auth 값으로 init 쪽에 두는게 맞음.
    LaunchedEffect(key1 = true) {
        viewModel.loadUserBooks(userId = "iK4v1WW1ZX4gID2HueBi")
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Home Screen")

        // ui
        when{
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
                // 성공적으로 책 데이터를 받아온 경우
                // 얘도 따로 파일 분리가 좋겠죠 ~ !!
                Column {
                    uiState.value.books.forEach { book ->
                        Text(text = book.title)// 간단하게 책 제목만 출력. 추후 BookCard 형태로 개선
                    }
                }
            }
        }

        // 테스트용 버튼 - 책 상세 페이지로 이동
        Button(
            onClick = {
                navController.navigate(NavigationRoute.BookDetail.createRoute("JfHQlFQZ7hceYq5kxB4b"))  // TODO 임시 고정 ID 입니다. 추후 실제 ID 값 넘기기
            }
        ) {
            Text("책 상세로 이동")
        }
    }
}