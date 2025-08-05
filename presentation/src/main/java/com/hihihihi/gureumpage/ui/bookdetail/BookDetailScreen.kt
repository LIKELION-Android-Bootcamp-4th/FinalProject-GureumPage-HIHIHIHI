package com.hihihihi.gureumpage.ui.bookdetail

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.hihihihi.gureumpage.designsystem.theme.GureumPageTheme
import com.hihihihi.gureumpage.ui.bookdetail.components.BookDetailFab
import com.hihihihi.gureumpage.ui.bookdetail.components.BookDetailTabs
import com.hihihihi.gureumpage.ui.bookdetail.components.BookSimpleInfoSection
import com.hihihihi.gureumpage.ui.bookdetail.components.BookStatisticsCard
import com.hihihihi.gureumpage.ui.bookdetail.components.ReadingProgressSection

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

    val scrollState: LazyListState = rememberLazyListState()

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        BookDetailFab(
            modifier = Modifier
                .align(alignment = Alignment.BottomEnd)
                .padding(bottom = 32.dp, end = 22.dp),
            onActionClick = {  }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = scrollState,
        ) {
            item { BookSimpleInfoSection() }
            item { ReadingProgressSection() }
            item { BookStatisticsCard() }
            item { BookDetailTabs() }
        }
    }
}

@Preview(name = "Light", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun BookDetailPreview() {
    GureumPageTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            BookDetailFab(
                modifier = Modifier
                    .align(alignment = Alignment.BottomEnd)
                    .padding(bottom = 32.dp, end = 22.dp),
                onActionClick = {  }
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
            ) {
                item { BookSimpleInfoSection() }
                item { ReadingProgressSection() }
                item { BookStatisticsCard() }
                item { BookDetailTabs() }
            }
        }
    }
}