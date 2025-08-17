package com.hihihihi.gureumpage.ui.timer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.hihihihi.domain.model.Quote
import com.hihihihi.domain.usecase.quote.AddQuoteUseCase
import com.hihihihi.domain.usecase.quote.GetQuoteByUserBookIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class MemoViewModel @Inject constructor(
    private val getQuotesByUserBookId: GetQuoteByUserBookIdUseCase,
    private val addQuote: AddQuoteUseCase,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _ui = MutableStateFlow(MemoUiState())
    val ui: StateFlow<MemoUiState> = _ui

    private var listenJob: Job? = null
    private var currentBookId: String? = null

    fun observe(userBookId: String) {
        if (currentBookId == userBookId) return
        currentBookId = userBookId
        listenJob?.cancel()

        val uid = auth.currentUser?.uid ?: "iK4v1WW1ZX4gID2HueBi"

        listenJob = viewModelScope.launch {
            _ui.update { it.copy(isLoading = true, error = null) }
            getQuotesByUserBookId(userBookId).collectLatest { list ->
                _ui.update { it.copy(items = list, isLoading = false, error = null) }
            }
        }
    }

    fun add(
        userBookId: String,
        pageNumber: Int?,
        content: String,
        title: String,
        author: String,
        imageUrl: String,
        publisher: String = "",
        onDone: () -> Unit = {}
    ) {
        val uid = auth.currentUser?.uid ?: "iK4v1WW1ZX4gID2HueBi"
        viewModelScope.launch {
            val q = Quote(
                id = "",
                userId = uid,
                userBookId = userBookId,
                content = content,
                pageNumber = pageNumber,
                isLiked = false,
                createdAt = LocalDateTime.now(),
                title = title,
                author = author,
                publisher = publisher,
                imageUrl = imageUrl
            )
            val r = addQuote(q)
            if (r.isSuccess) onDone()
            else _ui.update { it.copy(error = r.exceptionOrNull()?.message) }
        }
    }
}