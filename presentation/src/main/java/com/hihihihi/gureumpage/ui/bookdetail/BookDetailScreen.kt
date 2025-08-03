package com.hihihihi.gureumpage.ui.bookdetail

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.hihihihi.gureumpage.navigation.NavigationRoute

@Composable
fun BookDetailScreen(
    bookId: String,  // 상세 화면에 보여줄 책 ID
    navController: NavHostController,  // 네비게이션 컨트롤러
    snackbarHostState: SnackbarHostState,  // 스낵바 표시 상태
    viewModel: BookDetailViewModel = hiltViewModel() // Hilt로 주입된 ViewModel
) {
    // ViewModel에서 관리하는 UI 상태를 Compose State로 수집
    val uiState by viewModel.uiState.collectAsState()

    // 사용자 입력 내용과 페이지 번호를 상태로 기억
    var content by remember { mutableStateOf("") }
    var pageNumber by remember { mutableStateOf("") }

    // addQuoteState가 변할 때마다 실행되는 효과
    LaunchedEffect(uiState.addQuoteState) {
        val state = uiState.addQuoteState
        when {
            state.isSuccess -> {
                // 추가 성공 시 입력 필드 초기화
                content = ""
                pageNumber = ""
                Log.e("TAG", "BookDetailScreen: 성공!")

                // 성공 스낵바 표시
                snackbarHostState.showSnackbar("필사 추가 성공!")

                // 상태 초기화 (중복 알림 방지)
                viewModel.resetAddQuoteState()
            }

            state.error != null -> {
                // 에러 발생 시 로그 출력
                Log.e("TAG", "BookDetailScreen: 에러=${state.error}")

                // 에러 메시지 스낵바 표시
                snackbarHostState.showSnackbar("에러: ${state.error}")

                // 상태 초기화
                viewModel.resetAddQuoteState()
            }
        }
    }



    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("BookDetail Screen")

        // 타이머 화면으로 이동하는 버튼
        Button(
            onClick = {
                navController.navigate(NavigationRoute.Timer.route)
            }
        ) {
            Text("타이머로 이동")
        }

        // 마인드맵 화면으로 이동하는 버튼
        Button(
            onClick = {
                navController.navigate(NavigationRoute.MindMap.route)
            }
        ) {
            Text("마인드맵 이동")
        }

        // 필사 내용 입력 필드
        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("내용") },
            modifier = Modifier.fillMaxWidth()
        )

        // 페이지 번호 입력 필드 (숫자만 입력 가능)
        OutlinedTextField(
            value = pageNumber,
            onValueChange = { pageNumber = it.filter { ch -> ch.isDigit() } }, // 숫자 필터링
            label = { Text("페이지 번호") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        // 필사 추가 버튼
        Button(
            onClick = {
                // ViewModel의 명언 추가 함수 호출
                viewModel.addQuote(bookId, content, pageNumber.toIntOrNull())
            },
            // 로딩 중이 아니고, 내용이 비어있지 않을 때만 활성화
            enabled = !uiState.addQuoteState.isLoading && content.isNotBlank()
        ) {
            if (uiState.addQuoteState.isLoading) {
                // 로딩 중엔 원형 프로그레스 표시
                CircularProgressIndicator(
                    color = Color.White
                )
                // 평상시 버튼 텍스트
            } else Text("필사 추가")
        }


    }

}