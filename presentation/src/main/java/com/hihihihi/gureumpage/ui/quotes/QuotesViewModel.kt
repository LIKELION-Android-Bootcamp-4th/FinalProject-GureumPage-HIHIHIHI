package com.hihihihi.gureumpage.ui.quotes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.hihihihi.domain.model.Quote
import com.hihihihi.domain.usecase.quote.GetQuoteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuotesViewModel @Inject constructor(
    private val getQuoteUseCase: GetQuoteUseCase
) : ViewModel() {
    private val _quotes = MutableStateFlow<List<Quote>>(emptyList())
    val quotes: StateFlow<List<Quote>> = _quotes.asStateFlow()

    private val _uiState = MutableStateFlow(QuotesUiState(isLoading = true))
    val uiState: StateFlow<QuotesUiState> = _uiState.asStateFlow()

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val currentUid: String
        get() = auth.currentUser!!.uid

    init {
        getQuotes(currentUid)
    }

    /*fun getQuotes(userId: String) {
        viewModelScope.launch {
            getQuoteUseCase(userId).collect {
                _quotes.value = it
            }
        }
    }*/

    fun getQuotes(userId: String) {
        viewModelScope.launch {
            try {
                _uiState.value = QuotesUiState(isLoading = true)
                getQuoteUseCase(userId).collect { quotes ->
                    _uiState.value = QuotesUiState(quotes = quotes, isLoading = false)
                }
            } catch (e: Exception) {
                _uiState.value =
                    QuotesUiState(errorMessage = e.message ?: "알 수 없는 오류 발생", isLoading = false)
            }
        }
    }
}
