package com.hihihihi.presentation.ui.bookdetail

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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.hihihihi.domain.model.ReadingStatus
import com.hihihihi.presentation.designsystem.theme.GureumPageTheme
import com.hihihihi.presentation.ui.bookdetail.components.AddManualHistoryDialog
import com.hihihihi.presentation.ui.bookdetail.components.AddQuoteDialog
import com.hihihihi.presentation.ui.bookdetail.components.BookCompletionDialog
import com.hihihihi.presentation.ui.bookdetail.components.BookDetailFab
import com.hihihihi.presentation.ui.bookdetail.components.BookDetailTabs
import com.hihihihi.presentation.ui.bookdetail.components.BookSimpleInfoSection
import com.hihihihi.presentation.ui.bookdetail.components.BookStatisticsCard
import com.hihihihi.presentation.ui.bookdetail.components.EditQuoteDialog
import com.hihihihi.presentation.ui.bookdetail.components.ReadingProgressSection
import com.hihihihi.presentation.ui.bookdetail.components.ReviewSection
import com.hihihihi.presentation.ui.bookdetail.components.SetReadingStatusBottomSheet
import com.hihihihi.presentation.ui.bookdetail.mock.dummyUserBook
import com.hihihihi.presentation.ui.home.components.ErrorView
import com.hihihihi.presentation.ui.home.components.LoadingView
import com.hihihihi.presentation.ui.model.HistoryUiModel
import com.hihihihi.presentation.ui.model.QuoteUiModel
import com.hihihihi.presentation.ui.model.UserBookUiModel
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
    bookId: String,
    snackbarHostState: SnackbarHostState,
    onNavigateToMindmap: (bookId: String, mindmapId: String) -> Unit,
    onNavigateToTimer: (bookId: String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: BookDetailViewModel = hiltViewModel(),
    initialShowAddQuote: Boolean = false,
    initialShowAddManualRecord: Boolean = false,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    BookDetailEffect.NavigateToMindmap -> onNavigateToMindmap(bookId, bookId)
                    BookDetailEffect.NavigateToTimer -> onNavigateToTimer(bookId)
                }
            }
        }
    }

    LaunchedEffect(bookId) {
        viewModel.loadUserBookDetails(bookId)
        if (initialShowAddQuote) viewModel.onAddQuoteClick()
        if (initialShowAddManualRecord) viewModel.onAddManualHistoryClick()
    }

    LaunchedEffect(uiState.addQuoteState) {
        val state = uiState.addQuoteState
        when {
            state.isSuccess -> {
                Toast.makeText(context, "필사가 추가되었습니다!", Toast.LENGTH_SHORT).show()
                viewModel.resetAddQuoteState()
            }

            state.error != null -> {
                Toast.makeText(context, "에러: ${state.error}", Toast.LENGTH_SHORT).show()
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
                onReadingStatusClick = viewModel::onReadingStatusClick,
                onReviewSave = { rating, review -> viewModel.patchReview(rating, review) },
                onQuoteEdit = { quoteId ->
                    val quoteUiModel = uiState.quotes.find { it.id == quoteId }
                    if (quoteUiModel != null) viewModel.onQuoteEditClick(quoteUiModel)
                },
                onQuoteDelete = { id -> viewModel.deleteQuote(id) },
                onAddQuoteClick = viewModel::onAddQuoteClick,
                onAddManualHistoryClick = viewModel::onAddManualHistoryClick,
                onNavigateToMindmap = viewModel::navigateToMindmap,
                onNavigateToTimer = viewModel::navigateToTimer,
            )
        }

        else -> {
            // TODO 빈 화면 또는 초기 화면
        }
    }

    when (val dialogState = uiState.dialogState) {
        BookDetailDialogState.None -> Unit

        is BookDetailDialogState.AddQuote -> {
            AddQuoteDialog(
                onDismiss = viewModel::dismissDialog,
                onSave = { pageNumber, content ->
                    viewModel.addQuote(bookId, content, pageNumber?.toIntOrNull())
                },
                lastPage = dialogState.lastPage,
            )
        }

        is BookDetailDialogState.AddManualHistory -> {
            AddManualHistoryDialog(
                currentPage = dialogState.currentPage,
                lastPage = dialogState.lastPage,
                startDate = dialogState.startDate,
                onDismiss = viewModel::dismissDialog,
                onSave = { date, startTime, endTime, readTime, readPageCount, currentPage ->
                    viewModel.addManualHistory(date, startTime, endTime, readTime, readPageCount, currentPage)
                    viewModel.dismissDialog()
                },
            )
        }

        BookDetailDialogState.ReadingStatus -> {
            if (uiState.userBook != null) {
                SetReadingStatusBottomSheet(
                    userBook = uiState.userBook!!,
                    sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                    onDismiss = viewModel::dismissDialog,
                    onConfirm = { status, page, startDate, endDate ->
                        viewModel.patchUserBook(status, page, startDate, endDate)
                        viewModel.dismissDialog()
                    },
                )
            }
        }

        is BookDetailDialogState.EditQuote -> {
            EditQuoteDialog(
                initialContent = dialogState.quoteUiModel.content,
                initialPageNumber = dialogState.quoteUiModel.pageNumber,
                onDismiss = viewModel::dismissDialog,
                onSave = { newContent, newPageNumber ->
                    viewModel.updateQuote(
                        quoteId = dialogState.quoteUiModel.id,
                        newContent = newContent,
                        newPageNumber = newPageNumber,
                    )
                    viewModel.dismissDialog()
                },
            )
        }

        BookDetailDialogState.Completion -> {
            if (uiState.userBook != null) {
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
                            endDate = endDate,
                        )
                        viewModel.dismissDialog()
                        Toast.makeText(context, "🎉 완독을 축하드립니다!", Toast.LENGTH_LONG).show()
                    },
                    onDismiss = viewModel::dismissDialog,
                )
            }
        }
    }
}

@Composable
fun BookDetailContent(
    userBook: UserBookUiModel,
    quotes: List<QuoteUiModel>,
    histories: List<HistoryUiModel>,
    bookStatistic: BookStatistic,
    onReadingStatusClick: () -> Unit,
    onAddQuoteClick: () -> Unit = {},
    onAddManualHistoryClick: () -> Unit = {},
    onNavigateToMindmap: () -> Unit = {},
    onNavigateToTimer: () -> Unit = {},
    onReviewSave: (Double, String) -> Unit = { _, _ -> },
    onQuoteEdit: (String) -> Unit = {},
    onQuoteDelete: (String) -> Unit = {},
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
                        onSave = { rating, review -> onReviewSave(rating, review) },
                    )
                }
            }
            item {
                BookDetailTabs(
                    userBook = userBook,
                    quotes = quotes,
                    histories = histories,
                    onQuoteEdit = onQuoteEdit,
                    onQuoteDelete = onQuoteDelete,
                )
                Spacer(Modifier.height(50.dp))
            }
        }

        BookDetailFab(
            readingStatus = userBook.status,
            onAddQuoteClick = onAddQuoteClick,
            onAddManualHistoryClick = onAddManualHistoryClick,
            onNavigateToMindmap = onNavigateToMindmap,
            onNavigateToTimer = onNavigateToTimer,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding(),
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
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
            ) {
                item { BookSimpleInfoSection(dummyUserBook, {}) }
                item { ReadingProgressSection(dummyUserBook) }
                item { BookStatisticsCard(BookStatistic("", "", "")) }
            }
        }
    }
}
