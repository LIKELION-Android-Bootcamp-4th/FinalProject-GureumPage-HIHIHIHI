package com.hihihihi.gureumpage.ui.timer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.hihihihi.domain.model.Quote
import com.hihihihi.domain.usecase.quote.AddQuoteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class MemoViewModel @Inject constructor(
    private val addQuote: AddQuoteUseCase,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _ui = MutableStateFlow(MemoUiState())
    val ui: StateFlow<MemoUiState> = _ui

    fun clear() {
        _ui.value = MemoUiState()
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
        val uid = auth.currentUser?.uid
        if (uid.isNullOrBlank()) {
            _ui.update { it.copy(error = "AUTH_REQUIRED") }
            onDone()
            return
        }
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
                imageUrl = imageUrl,
            )
            val r = addQuote(q)
            if (r.isSuccess) {
                _ui.update { it.copy(items = it.items + q) }
                onDone()
            } else _ui.update {
                it.copy(error = r.exceptionOrNull()?.message)
            }
        }
    }
}