package com.hihihihi.presentation.ui.quotes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hihihihi.domain.usecase.auth.GetCurrentUserIdUseCase
import com.hihihihi.domain.usecase.quote.GetQuoteUseCase
import com.hihihihi.presentation.ui.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuotesViewModel @Inject constructor(
    private val getQuoteUseCase: GetQuoteUseCase,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(QuotesUiState(isLoading = true))
    val uiState: StateFlow<QuotesUiState> = _uiState.asStateFlow()

    private val currentUid: String?
        get() = getCurrentUserIdUseCase()

    init {
        currentUid?.let { getQuotes(it) }
    }

    fun getQuotes(userId: String) {
        viewModelScope.launch {
            try {
                _uiState.value = QuotesUiState(isLoading = true)
                getQuoteUseCase(userId).collect { quotes ->
                    _uiState.value = QuotesUiState(quotes = quotes.map { it.toUiModel() }, isLoading = false)
                }
            } catch (e: Exception) {
                _uiState.value =
                    QuotesUiState(errorMessage = e.message ?: "알 수 없는 오류 발생", isLoading = false)
            }
        }
    }
}
