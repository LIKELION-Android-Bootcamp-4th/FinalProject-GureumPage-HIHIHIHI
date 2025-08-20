package com.hihihihi.gureumpage.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.hihihihi.domain.usecase.userbook.GetUserBooksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val getUserBooksUseCase: GetUserBooksUseCase // 유저 책 목록 가져오는 UseCase
) : ViewModel() {

    // ui 상태 :
    private val _uiState = MutableStateFlow(LibraryUiState(isLoading = true))
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    val userId = FirebaseAuth.getInstance().currentUser!!.uid

    init {
        loadUserBooks(userId)
    }

    // 유저 ID에 해당하는 책 목록을 비동기로 가져와 ui 상태에 반영
    fun loadUserBooks(userId: String) {
        viewModelScope.launch {
            try {
                //로딩 상태
                _uiState.value = LibraryUiState(isLoading = true)
                //책 목록 Flow 수집해서 ui 상태 갱신
                getUserBooksUseCase(userId).collect { books ->
                    _uiState.value = LibraryUiState(
                        isLoading = false,
                        books = books
                    )
                }
            } catch (e: Exception) {
                //에러 발생 시 ui 상태에 에러 메시지 전달
                _uiState.value = LibraryUiState(
                    isLoading = false,
                    errorMessage = e.message ?: "알 수 없는 오류 발생"
                )
            }
        }
    }
}