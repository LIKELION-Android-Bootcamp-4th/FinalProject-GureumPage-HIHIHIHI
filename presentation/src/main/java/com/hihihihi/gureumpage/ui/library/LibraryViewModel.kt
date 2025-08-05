package com.hihihihi.gureumpage.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hihihihi.domain.model.UserBook
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
): ViewModel(){

    // 책 목록 상태값(StateFlow)
    private val _userBooks = MutableStateFlow<List<UserBook>>(emptyList())
    val userBooks: StateFlow<List<UserBook>> = _userBooks.asStateFlow()

    // 유저 ID에 해당하는 책 목록 로드
    fun loadUserBooks(userId: String) {
        viewModelScope.launch {
            getUserBooksUseCase(userId).collect {
                _userBooks.value = it
            }
        }
    }
}