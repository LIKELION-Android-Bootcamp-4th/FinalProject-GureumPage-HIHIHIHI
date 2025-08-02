package com.hihihihi.gureumpage.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hihihihi.domain.model.DomainUserBook
import com.hihihihi.domain.usecase.userbook.GetUserBooksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getUserBooksUseCase: GetUserBooksUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    private val _userBooks = MutableStateFlow<List<DomainUserBook>>(emptyList())
    val userBooks: StateFlow<List<DomainUserBook>> = _userBooks

    fun loadUserBooks(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            getUserBooksUseCase(userId)
                .catch { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = e.message
                        )
                    }
                }.collect { books ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            books = books,
                            isEmpty = books.isEmpty()
                        )
                    }
                }
        }
    }
}