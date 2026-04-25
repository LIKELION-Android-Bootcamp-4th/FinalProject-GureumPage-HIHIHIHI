package com.hihihihi.presentation.ui.quotes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hihihihi.domain.usecase.auth.GetCurrentUserIdUseCase
import com.hihihihi.domain.usecase.quote.GetQuoteUseCase
import com.hihihihi.presentation.ui.model.QuoteUiModel
import com.hihihihi.presentation.ui.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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

    fun selectQuote(quote: QuoteUiModel?) {
        _uiState.update { it.copy(selectedQuote = quote) }
    }

    fun getQuotes(userId: String) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                getQuoteUseCase(userId).collect { quotes ->
                    _uiState.update { it.copy(quotes = quotes.map { quote -> quote.toUiModel() }, isLoading = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message ?: "알 수 없는 오류 발생", isLoading = false) }
            }
        }
    }
}
