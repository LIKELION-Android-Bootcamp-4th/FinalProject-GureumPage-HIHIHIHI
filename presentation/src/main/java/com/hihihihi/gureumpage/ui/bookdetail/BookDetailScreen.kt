package com.hihihihi.gureumpage.ui.bookdetail

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.hihihihi.domain.model.History
import com.hihihihi.domain.model.Quote
import com.hihihihi.domain.model.ReadingStatus
import com.hihihihi.domain.model.UserBook
import com.hihihihi.gureumpage.designsystem.theme.GureumPageTheme
import com.hihihihi.gureumpage.navigation.NavigationRoute
import com.hihihihi.gureumpage.ui.bookdetail.components.AddManualHistoryDialog
import com.hihihihi.gureumpage.ui.bookdetail.components.AddQuoteDialog
import com.hihihihi.gureumpage.ui.bookdetail.components.BookDetailFab
import com.hihihihi.gureumpage.ui.bookdetail.components.BookDetailTabs
import com.hihihihi.gureumpage.ui.bookdetail.components.BookSimpleInfoSection
import com.hihihihi.gureumpage.ui.bookdetail.components.BookStatisticsCard
import com.hihihihi.gureumpage.ui.bookdetail.components.ReadingProgressSection
import com.hihihihi.gureumpage.ui.bookdetail.components.ReviewSection
import com.hihihihi.gureumpage.ui.bookdetail.components.SetReadingStatusBottomSheet
import com.hihihihi.gureumpage.ui.bookdetail.mock.dummyRecords
import com.hihihihi.gureumpage.ui.bookdetail.mock.dummyUserBook
import com.hihihihi.gureumpage.ui.home.mock.dummyQuotes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
    bookId: String,  // 상세 화면에 보여줄 책 ID
    navController: NavHostController,  // 네비게이션 컨트롤러
    snackbarHostState: SnackbarHostState,  // 스낵바 표시 상태
    viewModel: BookDetailViewModel = hiltViewModel() // Hilt로 주입된 ViewModel
) {
    // ViewModel에서 관리하는 UI 상태를 Compose State로 수집
    val uiState by viewModel.uiState.collectAsState()

    var showAddQuoteDialog by remember { mutableStateOf(false) }
    var showAddManualHistoryDialog by remember { mutableStateOf(false) }
    var showReadingStatusSheet by remember { mutableStateOf(false) }

    LaunchedEffect(bookId) {
        viewModel.loadUserBookDetails(bookId)
    }

    // addQuoteState가 변할 때마다 실행되는 효과
    LaunchedEffect(uiState.addQuoteState) {
        val state = uiState.addQuoteState
        when {
            state.isSuccess -> {
                // 추가 성공 시 입력 필드 초기화
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

    when {
        uiState.isLoading -> {
            // TODO 로딩 UI
        }

        uiState.errorMessage != null -> {
            // TODO 에러 UI
        }

        uiState.userBook != null -> {
            BookDetailContent(
                userBook = uiState.userBook!!,
                quotes = uiState.quotes,
                histories = uiState.histories,
                bookStatistic = viewModel.getStatistic(),
                onReadingStatusClick = { showReadingStatusSheet = true },
                onReviewSave = { rating, review ->
                    viewModel.patchReview(rating, review)
                },
                onEvent = { event ->
                    when (event) {
                        BookDetailFabEvent.NavigateToMindmap -> navController.navigate(
                            NavigationRoute.MindMap.createRoute(bookId, bookId)
                        )

                        BookDetailFabEvent.NavigateToTimer -> navController.navigate(
                            NavigationRoute.Timer.createRoute(bookId)
                        )

                        BookDetailFabEvent.ShowAddQuoteDialog -> showAddQuoteDialog = true
                        BookDetailFabEvent.ShowAddManualHistoryDialog -> showAddManualHistoryDialog = true
                    }
                }
            )
        }

        else -> {
            // TODO 빈 화면 또는 초기 화면
        }
    }

    if (showAddQuoteDialog) {
        AddQuoteDialog(
            onDismiss = { showAddQuoteDialog = false },
            onSave = { pageNumber, content ->
                viewModel.addQuote(bookId, content, pageNumber?.toIntOrNull())
            }
        )
    }

    if (showAddManualHistoryDialog) {
        uiState.userBook?.let {
            AddManualHistoryDialog(
                currentPage = it.currentPage,
                lastPage = it.totalPage,
                onDismiss = { showAddManualHistoryDialog = false },
                onSave = { date, startTime, endTime, readTime, readPageCount ->
                    viewModel.addManualHistory(
                        date,
                        startTime,
                        endTime,
                        readTime,
                        readPageCount
                    )
                    showAddManualHistoryDialog = false
                }
            )
        }
    }

    if (showReadingStatusSheet && uiState.userBook != null) {
        SetReadingStatusBottomSheet(
            userBook = uiState.userBook!!,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            onDismiss = { showReadingStatusSheet = false },
            onConfirm = { status, page, startDate, endDate ->
                // 상태 변경 로직 호출
                viewModel.patchUserBook(status, page, startDate, endDate)
                showReadingStatusSheet = false
            }
        )
    }
}

@Composable
fun BookDetailContent(
    userBook: UserBook,
    quotes: List<Quote>,
    histories: List<History>,
    bookStatistic: BookStatistic,
    onReadingStatusClick: () -> Unit,
    onEvent: (BookDetailFabEvent) -> Unit = {},
    onReviewSave: (Double, String) -> Unit = { _, _ -> }
) {
    val scrollState = rememberLazyListState()

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = scrollState,
        ) {
            item { BookSimpleInfoSection(userBook, onReadingStatusClick) }
            if(userBook.status != ReadingStatus.PLANNED) {
                item { ReadingProgressSection(userBook) }
                item { BookStatisticsCard(bookStatistic) }
            }
            if(userBook.status == ReadingStatus.FINISHED || userBook.review != null || userBook.rating != null){
                item {
                    ReviewSection(
                        initialRating = userBook.rating?.toFloat() ?: 0f,
                        initialReview = userBook.review ?: "",
                        onSave = { rating, review ->
                            onReviewSave(rating, review)
                        }
                    )
                }
            }
            item { BookDetailTabs(userBook, quotes, histories) }
        }

        // FAB
        BookDetailFab(
            userBook.status,
            onEvent = onEvent,
            modifier = Modifier
                .align(Alignment.BottomEnd)
        )
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
                ReadingStatus.READING,
                modifier = Modifier
                    .align(alignment = Alignment.BottomEnd)
                    .padding(bottom = 32.dp, end = 22.dp),
                onEvent = { }
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
            ) {
                item { BookSimpleInfoSection(dummyUserBook, {}) }
                item { ReadingProgressSection(dummyUserBook) }
                item { BookStatisticsCard(BookStatistic("", "", "")) }
                item { BookDetailTabs(dummyUserBook, dummyQuotes, dummyRecords) }
            }
        }
    }
}