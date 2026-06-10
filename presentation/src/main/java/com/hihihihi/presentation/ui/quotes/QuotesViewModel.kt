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
    private val _uiState = MutableStateFlow<QuotesUiState>(QuotesUiState.Loading)
    val uiState: StateFlow<QuotesUiState> = _uiState.asStateFlow()

    private val currentUid: String?
        get() = getCurrentUserIdUseCase()

    init {
        currentUid?.let { getQuotes(it) }
    }

    fun selectQuote(quote: QuoteUiModel?) {
        updateContent { it.copy(selectedQuote = quote) }
    }

    fun getQuotes(userId: String) {
        viewModelScope.launch {
            try {
                _uiState.update { QuotesUiState.Loading }
                getQuoteUseCase(userId).collect { quotes ->
                    updateContent { it.copy(quotes = quotes.map { quote -> quote.toUiModel() }) }
                }
            } catch (e: Exception) {
                _uiState.update { current ->
                    QuotesUiState.Error(
                        message = e.message ?: "알 수 없는 오류 발생",
                        previous = current as? QuotesUiState.Content,
                    )
                }
            }
        }
    }

    private inline fun updateContent(
        crossinline transform: (QuotesUiState.Content) -> QuotesUiState.Content,
    ) {
        _uiState.update { current -> transform(current.contentOrDefault()) }
    }
}
