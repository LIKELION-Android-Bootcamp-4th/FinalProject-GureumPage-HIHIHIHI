package com.hihihihi.presentation.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hihihihi.domain.usecase.auth.GetCurrentUserIdUseCase
import com.hihihihi.domain.usecase.userbook.GetUserBooksUseCase
import com.hihihihi.presentation.ui.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val getUserBooksUseCase: GetUserBooksUseCase, // 유저 책 목록 가져오는 UseCase
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
) : ViewModel() {

    // ui 상태 :
    private val _uiState = MutableStateFlow<LibraryUiState>(LibraryUiState.Loading)
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    val userId: String? = getCurrentUserIdUseCase()

    init {
        if (userId == null) {
            _uiState.value = LibraryUiState.Content()
        } else {
            loadUserBooks(userId)
        }
    }

    // 유저 ID에 해당하는 책 목록을 비동기로 가져와 ui 상태에 반영
    fun loadUserBooks(userId: String) {
        viewModelScope.launch {
            try {
                // 로딩 상태
                _uiState.update { LibraryUiState.Loading }
                // 책 목록 Flow 수집해서 ui 상태 갱신
                getUserBooksUseCase(userId).collect { books ->
                    _uiState.update { LibraryUiState.Content(books = books.map { book -> book.toUiModel() }) }
                }
            } catch (e: Exception) {
                // 에러 발생 시 ui 상태에 에러 메시지 전달
                _uiState.update { current ->
                    LibraryUiState.Error(
                        message = e.message ?: "알 수 없는 오류 발생",
                        previous = current as? LibraryUiState.Content,
                    )
                }
            }
        }
    }
}
