package com.hihihihi.gureumpage.ui.bookdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hihihihi.domain.model.Quote
import com.hihihihi.domain.usecase.quote.AddQuoteUseCase
import com.hihihihi.domain.usecase.quote.GetQuoteUseCase
import com.hihihihi.domain.usecase.userbook.GetUserBookUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class BookDetailViewModel @Inject constructor(
    private val addQuoteUseCase: AddQuoteUseCase,
    private val getUseBookUseCase: GetUserBookUseCase,
):ViewModel(){

    // UI 상태를 관리하는 StateFlow
    private val _uiState = MutableStateFlow(BookDetailUiState())
    val uiState: StateFlow<BookDetailUiState> = _uiState

    fun loadUserBookDetails(userBookId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                getUseBookUseCase(userBookId).collect { userBook ->
                    _uiState.update { it.copy(userBook = userBook, isLoading = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message ?: "알 수 없는 오류", isLoading = false) }
            }
        }
    }




    /**
     * 필사 추가 기능
     *
     * @param userBookId 사용자 책 ID
     * @param content 명언 내용
     * @param pageNumber 해당 페이지 번호 (nullable)
     */
    fun addQuote(userBookId: String, content: String, pageNumber: Int?){
        viewModelScope.launch {
            // 로딩 상태로 설정
            _uiState.value = _uiState.value.copy(addQuoteState = AddQuoteState(isLoading = true))

            val userId = "iK4v1WW1ZX4gID2HueBi" //TODO 로그인 구현 후 Auth에서 currentUser 가져오는 식으로 변경

            // 새로운 quote 객체 생성
            val newQuote = Quote(
                id = "",
                userId = userId,
                userBookId = userBookId,
                content = content,
                pageNumber = pageNumber,
                isLiked = false,
                createdAt = LocalDateTime.now(),
                title = "",
                author = "",
                publisher = "",
                imageUrl = ""
            )
            // 명언 추가 UseCase 실행
            val result = addQuoteUseCase(newQuote)

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
}