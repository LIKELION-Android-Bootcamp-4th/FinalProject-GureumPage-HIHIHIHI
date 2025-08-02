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

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val uiState = viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadUserBooks(userId = "iK4v1WW1ZX4gID2HueBi")
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Home Screen")

        when{
            uiState.value.isLoading -> {
                Text("로딩중...")
            }
            uiState.value.errorMessage != null -> {
                Text("에러 발생: ${uiState.value.errorMessage}")
            }
            uiState.value.isEmpty -> {
                Text("등록된 책이 없습니다.")
            }
            else -> {
                Column {
                    uiState.value.books.forEach { book ->
                        Text(text = book.title)
                    }
                }
            }
        }

        Button(
            onClick = {
                navController.navigate(NavigationRoute.BookDetail.createRoute("JfHQlFQZ7hceYq5kxB4b"))  // TODO 임시 고정 ID 입니다. 추후 실제 ID 값 넘기기
            }
        ) {
            Text("책 상세로 이동")
        }
    }
}