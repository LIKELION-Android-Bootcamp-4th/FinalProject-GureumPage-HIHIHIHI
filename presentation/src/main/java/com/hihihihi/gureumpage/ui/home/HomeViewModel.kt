package com.hihihihi.gureumpage.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hihihihi.domain.model.ReadingStatus
import com.hihihihi.domain.model.UserBook
import com.hihihihi.domain.usecase.userbook.GetUserBooksByStatusUseCase
import com.hihihihi.domain.usecase.userbook.GetUserBooksUseCase
import com.hihihihi.gureumpage.ui.home.mock.mockUserBooks
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getUserBooksUseCase: GetUserBooksUseCase, // 유저 책 데이터를 가져오는 UseCase 주입
    private val getUserBooksByStatusUseCase: GetUserBooksByStatusUseCase
) : ViewModel() {

    // UI 상태를 관리하는 StateFlow (내부 업데이트는 _uiState, 외부에선 uiState만 노출)
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState


    init {
        // ⚠️ 현재는 하드코딩된 userId → 추후에는 FirebaseAuth로부터 userId를 가져와야 함
        loadUserBooks("iK4v1WW1ZX4gID2HueBi")
    }

    // 유저 책 목록을 불러오는 함수
    fun loadUserBooks(userId: String) {
        viewModelScope.launch {
            // 로딩 시작 상태로 UI 갱신
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // 유저의 책 리스트를 가져옴
            getUserBooksByStatusUseCase(userId, ReadingStatus.READING)
                .catch { e ->
                    // 에러 발생 시 UI 상태에 에러 메시지 반영, 현재는 간단하게만 처리해둠
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = e.message
                        )
                    }
                }.collect { books ->
                    // 성공 시 UI 상태에 책 리스트 반영
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            books = books,
                        )
                    }
                }
        }
    }
}
