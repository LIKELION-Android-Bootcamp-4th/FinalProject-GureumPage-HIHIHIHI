package com.hihihihi.gureumpage.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.hihihihi.domain.model.ReadingStatus
import com.hihihihi.domain.model.UserBook
import com.hihihihi.domain.usecase.quote.GetQuoteUseCase
import com.hihihihi.domain.usecase.user.GetHomeDataUseCase
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
    private val getHomeDataUseCase: GetHomeDataUseCase
) : ViewModel() {

    // UI 상태를 관리하는 StateFlow (내부 업데이트는 _uiState, 외부에선 uiState만 노출)
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState


    init {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid

        viewModelScope.launch {
            getHomeDataUseCase(userId)
                .catch { e ->
                    _uiState.update {
                        it.copy(errorMessage = e.message, isLoading = false)
                    }
                }
                .collect { homeData ->
                    _uiState.update {
                        it.copy(homeData = homeData, isLoading = false)
                    }
                }
        }
    }
}
