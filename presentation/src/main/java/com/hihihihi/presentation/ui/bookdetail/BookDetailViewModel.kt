package com.hihihihi.presentation.ui.bookdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hihihihi.domain.model.History
import com.hihihihi.domain.model.Quote
import com.hihihihi.domain.model.ReadingStatus
import com.hihihihi.domain.model.RecordType
import com.hihihihi.domain.model.UserBook
import com.hihihihi.domain.usecase.auth.GetCurrentUserIdUseCase
import com.hihihihi.domain.usecase.history.AddHistoryUseCase
import com.hihihihi.domain.usecase.quote.AddQuoteUseCase
import com.hihihihi.domain.usecase.quote.DeleteQuoteUseCase
import com.hihihihi.domain.usecase.quote.UpdateQuoteUseCase
import com.hihihihi.domain.usecase.userbook.GetBookDetailDataUseCase
import com.hihihihi.domain.usecase.userbook.PatchUserBookUseCase
import com.hihihihi.presentation.ui.model.QuoteUiModel
import com.hihihihi.presentation.ui.model.toUiModel
import com.hihihihi.presentation.utils.formatSecondsToReadableTime
import com.hihihihi.presentation.utils.getDailyAverageReadTimeInSeconds
import com.hihihihi.presentation.utils.getDayCountLabel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class BookDetailViewModel @Inject constructor(
    private val addQuoteUseCase: AddQuoteUseCase,
    private val addHistoryUseCase: AddHistoryUseCase,
    private val patchUserBookUseCase: PatchUserBookUseCase,
    private val getBookDetailDataUseCase: GetBookDetailDataUseCase,
    private val deleteQuoteUseCase: DeleteQuoteUseCase,
    private val updateQuoteUseCase: UpdateQuoteUseCase,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<BookDetailUiState>(BookDetailUiState.Loading)
    val uiState: StateFlow<BookDetailUiState> = _uiState

    private val _effect = Channel<BookDetailEffect>(Channel.BUFFERED)
    val effect: Flow<BookDetailEffect> = _effect.receiveAsFlow()

    // domain 백킹 — mutation 연산(patchUserBook, addQuote, addManualHistory)에서 사용
    private var _domainUserBook: UserBook? = null
    private var _domainHistories: List<History> = emptyList()

    private val currentUid: String?
        get() = getCurrentUserIdUseCase()

    fun loadUserBookDetails(userBookId: String) {
        viewModelScope.launch {
            getBookDetailDataUseCase(userBookId)
                .onStart {
                    _uiState.update { current ->
                        val content = current.contentOrDefault()
                        if (content.dialogState == BookDetailDialogState.None) {
                            BookDetailUiState.Loading
                        } else {
                            content
                        }
                    }
                }
                .catch { e ->
                    _uiState.update { current ->
                        BookDetailUiState.Error(
                            message = e.message ?: "책 정보를 가져오는데 실패했어요",
                            previous = current as? BookDetailUiState.Content,
                        )
                    }
                }
                .collect { data ->
                    val userBook = data.userBook
                    _domainUserBook = userBook
                    _domainHistories = data.history

                    val shouldShowCompletion = userBook.status == ReadingStatus.READING &&
                            userBook.currentPage >= userBook.totalPage &&
                            userBook.currentPage > 0 &&
                            userBook.totalPage > 0

                    _uiState.update {
                        val current = it.contentOrDefault()
                        BookDetailUiState.Content(
                            userBook = userBook.toUiModel(),
                            quotes = data.quotes.map { q -> q.toUiModel() },
                            histories = data.history.map { h -> h.toUiModel() },
                            dialogState = if (shouldShowCompletion && current.dialogState == BookDetailDialogState.None) {
                                BookDetailDialogState.Completion
                            } else {
                                current.dialogState
                            },
                        )
                    }
                }
        }
    }

    // --- Dialog 상태 메서드 ---

    fun onAddQuoteClick() {
        updateContent { it.copy(dialogState = BookDetailDialogState.AddQuote(it.userBook?.totalPage)) }
    }

    fun onAddManualHistoryClick() {
        val userBook = _domainUserBook
        updateContent {
            it.copy(
                dialogState = BookDetailDialogState.AddManualHistory(
                    currentPage = userBook?.currentPage ?: 0,
                    lastPage = userBook?.totalPage ?: 0,
                    startDate = userBook?.startDate,
                ),
            )
        }
    }

    fun onReadingStatusClick() {
        updateContent { it.copy(dialogState = BookDetailDialogState.ReadingStatus) }
    }

    fun onQuoteEditClick(quoteUiModel: QuoteUiModel) {
        updateContent { it.copy(dialogState = BookDetailDialogState.EditQuote(quoteUiModel)) }
    }

    fun dismissDialog() {
        updateContent { it.copy(dialogState = BookDetailDialogState.None) }
    }

    // --- Navigation Effect 메서드 ---

    fun navigateToMindmap() {
        viewModelScope.launch { _effect.send(BookDetailEffect.NavigateToMindmap) }
    }

    fun navigateToTimer() {
        viewModelScope.launch { _effect.send(BookDetailEffect.NavigateToTimer) }
    }

    // --- 데이터 조작 메서드 ---

    fun addManualHistory(
        date: LocalDateTime,
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        readTime: Int,
        readPageCount: Int,
        currentPage: Int,
    ) {
        val userBook = _domainUserBook ?: return
        val uid = currentUid ?: return

        val history = History(
            id = "",
            userId = uid,
            userBookId = userBook.userBookId,
            date = date,
            startTime = startTime,
            endTime = endTime,
            readTime = readTime,
            readPageCount = readPageCount,
            recordType = RecordType.MANUAL,
        )

        viewModelScope.launch {
            try {
                addHistoryUseCase(history, currentPage = currentPage)
            } catch (e: Exception) {
                _effect.send(BookDetailEffect.ShowMessage(e.message ?: "독서 기록을 저장하지 못했습니다."))
            }
        }
    }

    fun addQuote(userBookId: String, content: String, pageNumber: Int?) {
        val userBook = _domainUserBook ?: return
        val uid = currentUid ?: return

        val newQuote = Quote(
            id = "",
            userId = uid,
            userBookId = userBookId,
            content = content,
            pageNumber = pageNumber,
            isLiked = false,
            createdAt = LocalDateTime.now(),
            title = userBook.title,
            author = userBook.author,
            publisher = userBook.publisher ?: "",
            imageUrl = userBook.imageUrl,
        )
        viewModelScope.launch {
            val result = addQuoteUseCase(newQuote)
            if (result.isSuccess) {
                _effect.send(BookDetailEffect.ShowMessage("필사가 추가되었습니다!"))
            } else if (result.isFailure) {
                _effect.send(BookDetailEffect.ShowMessage("에러: ${result.exceptionOrNull()?.message ?: "알 수 없는 오류"}"))
            }
        }
    }

    fun resetAddQuoteState() {
        // Add quote result is delivered through BookDetailEffect.
    }

    fun getStatistic(): BookStatistic {
        val userBook = _domainUserBook
        return BookStatistic(
            readingPeriod = userBook?.let { book ->
                val startDate = book.startDate
                if (startDate == null) "아직 읽지 않은 책"
                else getDayCountLabel(startDate, book.endDate, book.status)
            } ?: "아직 읽지 않은 책",
            totalReadingTime = _domainHistories
                .sumOf { it.readTime }
                .let { if (it == 0) "0분" else formatSecondsToReadableTime(it) },
            averageDailyTime = getDailyAverageReadTimeInSeconds(_domainHistories),
        )
    }

    fun patchUserBook(status: ReadingStatus?, page: Int?, startDate: LocalDateTime?, endDate: LocalDateTime?) {
        val userBook = _domainUserBook ?: return
        val patchUserBook = userBook.copy(
            status = status ?: userBook.status,
            currentPage = page ?: userBook.currentPage,
            startDate = startDate ?: userBook.startDate,
            endDate = endDate ?: userBook.endDate,
        )
        viewModelScope.launch { patchUserBookUseCase(patchUserBook) }
    }

    fun patchReview(rating: Double, review: String) {
        val userBook = _domainUserBook ?: return
        val patchUserBook = userBook.copy(rating = rating, review = review)
        viewModelScope.launch { patchUserBookUseCase(patchUserBook) }
    }

    fun deleteQuote(quoteId: String) {
        viewModelScope.launch {
            val result = deleteQuoteUseCase(quoteId)
            if (result.isSuccess) {
                updateContent { state -> state.copy(quotes = state.quotes.filterNot { it.id == quoteId }) }
            } else {
                _effect.send(BookDetailEffect.ShowMessage(result.exceptionOrNull()?.message ?: "필사를 삭제하지 못했습니다."))
            }
        }
    }

    fun updateQuote(quoteId: String, newContent: String, newPageNumber: Int?) {
        val currentQuotes = uiState.value.contentOrDefault().quotes.toMutableList()
        val index = currentQuotes.indexOfFirst { it.id == quoteId }
        if (index != -1) {
            val updatedQuote = currentQuotes[index].copy(content = newContent, pageNumber = newPageNumber)
            currentQuotes[index] = updatedQuote
            updateContent { it.copy(quotes = currentQuotes) }
            viewModelScope.launch {
                val result = updateQuoteUseCase(quoteId, newContent, newPageNumber)
                if (result.isFailure) {
                    _effect.send(BookDetailEffect.ShowMessage(result.exceptionOrNull()?.message ?: "필사를 수정하지 못했습니다."))
                }
            }
        }
    }

    private inline fun updateContent(
        crossinline transform: (BookDetailUiState.Content) -> BookDetailUiState.Content,
    ) {
        _uiState.update { current -> transform(current.contentOrDefault()) }
    }
}

data class BookStatistic(
    val readingPeriod: String,
    val totalReadingTime: String,
    val averageDailyTime: String,
)

sealed interface BookDetailDialogState {
    data object None : BookDetailDialogState
    data class AddQuote(val lastPage: Int?) : BookDetailDialogState
    data class AddManualHistory(
        val currentPage: Int,
        val lastPage: Int,
        val startDate: LocalDateTime?,
    ) : BookDetailDialogState

    data object ReadingStatus : BookDetailDialogState
    data class EditQuote(val quoteUiModel: QuoteUiModel) : BookDetailDialogState
    data object Completion : BookDetailDialogState
}

sealed interface BookDetailEffect {
    data object NavigateToMindmap : BookDetailEffect
    data object NavigateToTimer : BookDetailEffect
    data class ShowMessage(val message: String) : BookDetailEffect
}
