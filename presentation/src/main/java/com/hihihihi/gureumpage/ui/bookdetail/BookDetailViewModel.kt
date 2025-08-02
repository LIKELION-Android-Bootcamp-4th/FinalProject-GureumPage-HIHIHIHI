package com.hihihihi.gureumpage.ui.bookdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hihihihi.domain.model.Quote
import com.hihihihi.domain.usecase.quote.AddQuoteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookDetailViewModel @Inject constructor(
    private val addQuoteUseCase: AddQuoteUseCase,
):ViewModel(){

    private val _uiState = MutableStateFlow(BookDetailUiState(null,null,addQuoteState = AddQuoteState()))
    val uiState: StateFlow<BookDetailUiState> = _uiState


    fun addQuote(userBookId: String, content: String, pageNumber: Int?){
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(addQuoteState = AddQuoteState(isLoading = true))

            val userId = "iK4v1WW1ZX4gID2HueBi" //TODO 로그인 구현 후 Auth에서 currentUser 가져오는 식으로 변경
            val newQuote = Quote(
                id = "",
                userId = userId,
                userBookId = userBookId,
                content = content,
                pageNumber = pageNumber,
                isLiked = false,
                createdAt = null
            )
            val result = addQuoteUseCase(newQuote)

            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(addQuoteState = AddQuoteState(isSuccess = true))
                delay(2000)
                _uiState.value = _uiState.value.copy(addQuoteState = AddQuoteState())
            } else if (result.isFailure) {
                _uiState.value = _uiState.value.copy(addQuoteState = AddQuoteState(error = result.exceptionOrNull()?.message ?: "알 수 없는 오류"))
            } else {
                _uiState.value = _uiState.value.copy(addQuoteState = AddQuoteState())
            }

        }
    }

    fun resetAddQuoteState() {
        _uiState.value = _uiState.value.copy(addQuoteState = AddQuoteState())
    }
}