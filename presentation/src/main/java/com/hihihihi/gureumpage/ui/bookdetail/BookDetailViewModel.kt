package com.hihihihi.gureumpage.ui.bookdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hihihihi.domain.model.Quote
import com.hihihihi.domain.model.ReadingStatus
import com.hihihihi.domain.usecase.quote.AddQuoteUseCase
import com.hihihihi.domain.usecase.userbook.GetBookDetailDataUseCase
import com.hihihihi.domain.usecase.userbook.PatchUserBookUseCase
import com.hihihihi.gureumpage.common.utils.formatSecondsToReadableTime
import com.hihihihi.gureumpage.common.utils.getDailyAverageReadTimeInSeconds
import com.hihihihi.gureumpage.common.utils.getDayCountLabel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class BookDetailViewModel @Inject constructor(
    private val addQuoteUseCase: AddQuoteUseCase,
    private val patchUserBookUseCase: PatchUserBookUseCase,
    private val getBookDetailDataUseCase: GetBookDetailDataUseCase,
):ViewModel(){

    // UI 상태를 관리하는 StateFlow
    private val _uiState = MutableStateFlow(BookDetailUiState())
    val uiState: StateFlow<BookDetailUiState> = _uiState

    fun loadUserBookDetails(userBookId: String) {
        viewModelScope.launch {
            getBookDetailDataUseCase(userBookId)
                .onStart { _uiState.update { it.copy(isLoading = true)   } }
                .catch { e -> _uiState.update { it.copy(errorMessage = e.message, isLoading = false) } }
                .collect{ data ->
                    _uiState.update {
                        it.copy(
                            userBook = data.userBook,
                            quotes = data.quotes,
                            histories = data.history,
                            isLoading = false
                        )
                    }
                }
        }
    }


    fun addQuote(userBookId: String, content: String, pageNumber: Int?, ){
        val userBook = uiState.value.userBook ?: return // 방어 코드

        val userId = "iK4v1WW1ZX4gID2HueBi" //TODO 로그인 구현 후 Auth에서 currentUser 가져오는 식으로 변경

        val newQuote = Quote(
            id = "",
            userId = userId,
            userBookId = userBookId,
            content = content,
            pageNumber = pageNumber,
            isLiked = false,
            createdAt = LocalDateTime.now(),
            title = userBook.title,
            author = userBook.author,
            publisher = "", //TODO userBook 수정하면 여기도 수정
            imageUrl = userBook.imageUrl
        )
        viewModelScope.launch {
            // 로딩 상태로 설정
            _uiState.value = _uiState.value.copy(addQuoteState = AddQuoteState(isLoading = true))

            // 명언 추가 UseCase 실행
            val result =  addQuoteUseCase(newQuote)

            // 결과에 따라 상태 업데이트
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(addQuoteState = AddQuoteState(isSuccess = true))
                delay(2000) // 잠깐 보여주기 위한 delay (필요에 따라 조절 가능)
                _uiState.value = _uiState.value.copy(addQuoteState = AddQuoteState())
            } else if (result.isFailure) {
                _uiState.value = _uiState.value.copy(
                    addQuoteState = AddQuoteState(
                        error = result.exceptionOrNull()?.message ?: "알 수 없는 오류"
                    )
                )
            }

        }
    }

    // 필사 추가 상태 초기화 함수
    fun resetAddQuoteState() {
        _uiState.value = _uiState.value.copy(addQuoteState = AddQuoteState())
    }

    // 통계 반환하는 함수
    fun getStatistic(): BookStatistic{
        return BookStatistic(
            readingPeriod = if(uiState.value.userBook?.startDate == null) "아직 읽지 않은 책" else getDayCountLabel(uiState.value.userBook?.startDate!!),
            totalReadingTime = if(uiState.value.userBook?.totalReadTime == null) "0분" else formatSecondsToReadableTime(uiState.value.userBook?.totalReadTime!!),
            averageDailyTime = getDailyAverageReadTimeInSeconds(uiState.value.histories)
        )
    }

    fun patchUserBook(status: ReadingStatus?, page: Int?, startDate: LocalDateTime?, endDate: LocalDateTime?){
        val userBook = uiState.value.userBook ?: return

        val patchUserBook = userBook.copy(
            status = status ?: userBook.status,
            currentPage = page ?: userBook.currentPage,
            startDate = startDate ?: userBook.startDate,
            endDate = endDate ?: userBook.endDate
        )

        viewModelScope.launch {
            patchUserBookUseCase(patchUserBook)
        }
    }
}

data class BookStatistic(
    val readingPeriod: String,
    val totalReadingTime: String,
    val averageDailyTime: String
)

