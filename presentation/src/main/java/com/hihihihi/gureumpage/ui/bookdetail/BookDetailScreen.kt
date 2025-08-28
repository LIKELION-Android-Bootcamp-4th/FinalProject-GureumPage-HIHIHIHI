package com.hihihihi.gureumpage.ui.bookdetail

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.ui.platform.LocalContext
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
import com.hihihihi.gureumpage.ui.bookdetail.components.BookCompletionDialog
import com.hihihihi.gureumpage.ui.bookdetail.components.BookDetailFab
import com.hihihihi.gureumpage.ui.bookdetail.components.BookDetailTabs
import com.hihihihi.gureumpage.ui.bookdetail.components.BookSimpleInfoSection
import com.hihihihi.gureumpage.ui.bookdetail.components.BookStatisticsCard
import com.hihihihi.gureumpage.ui.bookdetail.components.EditQuoteDialog
import com.hihihihi.gureumpage.ui.bookdetail.components.ReadingProgressSection
import com.hihihihi.gureumpage.ui.bookdetail.components.ReviewSection
import com.hihihihi.gureumpage.ui.bookdetail.components.SetReadingStatusBottomSheet
import com.hihihihi.gureumpage.ui.bookdetail.mock.dummyUserBook
import com.hihihihi.gureumpage.ui.home.components.ErrorView
import com.hihihihi.gureumpage.ui.home.components.LoadingView
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
    bookId: String,  // 상세 화면에 보여줄 책 ID
    navController: NavHostController,  // 네비게이션 컨트롤러
    snackbarHostState: SnackbarHostState,  // 스낵바 표시 상태
    viewModel: BookDetailViewModel = hiltViewModel(), // Hilt로 주입된 ViewModel
    initialShowAddQuote: Boolean = false,
    initialShowAddManualRecord: Boolean = false
) {
    // ViewModel에서 관리하는 UI 상태를 Compose State로 수집
    val uiState by viewModel.uiState.collectAsState()

    val context = LocalContext.current

    var showAddQuoteDialog by remember { mutableStateOf(initialShowAddQuote) }
    var showAddManualHistoryDialog by remember { mutableStateOf(initialShowAddManualRecord) }
    var showReadingStatusSheet by remember { mutableStateOf(false) }

    var showEditQuoteDialog by remember { mutableStateOf<Pair<String, Quote>?>(null) }

    var showCompletionDialog by remember { mutableStateOf(false) }

    LaunchedEffect(bookId) {
        viewModel.loadUserBookDetails(bookId)
    }

    LaunchedEffect(uiState.userBook) {
        val userBook = uiState.userBook
        if (userBook != null &&
            userBook.status == ReadingStatus.READING &&
            userBook.currentPage >= userBook.totalPage &&
            userBook.currentPage > 0 &&
            userBook.totalPage > 0
        ) {
            showCompletionDialog = true
        }
    }

    // addQuoteState가 변할 때마다 실행되는 효과
    LaunchedEffect(uiState.addQuoteState) {
        val state = uiState.addQuoteState
        when {
            state.isSuccess -> {
                // 성공 스낵바 표시
                Toast.makeText(context, "필사가 추가되었습니다!", Toast.LENGTH_SHORT).show()

                // 상태 초기화 (중복 알림 방지)
                viewModel.resetAddQuoteState()
            }

            state.error != null -> {
                // 에러 메시지 스낵바 표시
                Toast.makeText(context, "에러: ${state.error}", Toast.LENGTH_SHORT).show()

                // 상태 초기화
                viewModel.resetAddQuoteState()
            }
        }
    }

    when {
        uiState.isLoading -> {
            LoadingView()
        }

        uiState.errorMessage != null -> {
            ErrorView(message = "책 정보를 가져오는데 실패했어요")
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
                onQuoteEdit = { quoteId ->
                    val quote = uiState.quotes.find { it.id == quoteId }
                    if (quote != null) {
                        showEditQuoteDialog = quoteId to quote
                    }
                },
                onQuoteDelete = { id -> viewModel.deleteQuote(id) },
                onEvent = { event ->
                    when (event) {
                        BookDetailFabEvent.NavigateToMindmap -> navController.navigate(
                            NavigationRoute.MindMap.createRoute(bookId, bookId)
                        )

                        BookDetailFabEvent.NavigateToTimer -> navController.navigate(
                            NavigationRoute.Timer.createRoute(bookId)
                        )

                        BookDetailFabEvent.ShowAddQuoteDialog -> showAddQuoteDialog = true
                        BookDetailFabEvent.ShowAddManualHistoryDialog -> showAddManualHistoryDialog =
                            true
                    }
                }
            )
        }

        else -> {
            // TODO 빈 화면 또는 초기 화면
        }
    }

    showEditQuoteDialog?.let { (quoteId, quote) ->
        EditQuoteDialog(
            initialContent = quote.content,
            initialPageNumber = quote.pageNumber,
            onDismiss = { showEditQuoteDialog = null },
            onSave = { newContent, newPageNumber ->
                viewModel.updateQuote(
                    quoteId = quoteId,
                    newContent = newContent,
                    newPageNumber = newPageNumber
                )
                showEditQuoteDialog = null
            }
        )
    }

    if (showAddQuoteDialog) {
        AddQuoteDialog(
            onDismiss = { showAddQuoteDialog = false },
            onSave = { pageNumber, content ->
                viewModel.addQuote(bookId, content, pageNumber?.toIntOrNull())
            },
            lastPage = uiState.userBook?.totalPage
        )
    }

    if (showAddManualHistoryDialog) {
        uiState.userBook?.let {
            AddManualHistoryDialog(
                currentPage = it.currentPage,
                lastPage = it.totalPage,
                uiState.userBook?.startDate,
                onDismiss = { showAddManualHistoryDialog = false },
                onSave = { date, startTime, endTime, readTime, readPageCount, currentPage ->
                    viewModel.addManualHistory(
                        date,
                        startTime,
                        endTime,
                        readTime,
                        readPageCount,
                        currentPage
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

    if (showCompletionDialog && uiState.userBook != null) {
        BookCompletionDialog(
            userBook = uiState.userBook!!,
            onConfirm = {
                val userBook = uiState.userBook!!

                val endDate: LocalDateTime = uiState.histories
                    .mapNotNull { it.endTime }
                    .maxOrNull()
                    ?: LocalDateTime.now()

                viewModel.patchUserBook(
                    status = ReadingStatus.FINISHED,
                    page = userBook.totalPage,
                    startDate = userBook.startDate,
                    endDate = endDate
                )
                showCompletionDialog = false

                Toast.makeText(context, "🎉 완독을 축하드립니다!", Toast.LENGTH_LONG).show()
            },
            onDismiss = {
                showCompletionDialog = false
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
    onReviewSave: (Double, String) -> Unit = { _, _ -> },
    onQuoteEdit: (String) -> Unit = {},
    onQuoteDelete: (String) -> Unit = {}

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
            if (userBook.status != ReadingStatus.PLANNED) {
                item { ReadingProgressSection(userBook) }
                item { BookStatisticsCard(bookStatistic) }
            }
            if (userBook.status == ReadingStatus.FINISHED || userBook.review != null || userBook.rating != null) {
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
            item {
                BookDetailTabs(
                    userBook = userBook,
                    quotes = quotes,
                    histories = histories,
                    onQuoteEdit = onQuoteEdit,
                    onQuoteDelete = onQuoteDelete
                )
                Spacer(Modifier.height(50.dp))
            }
        }

        // FAB
        BookDetailFab(
            userBook.status,
            onEvent = onEvent,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
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
//                item { BookDetailTabs(dummyUserBook, dummyQuotes, dummyRecords) }
            }
        }
    }
}