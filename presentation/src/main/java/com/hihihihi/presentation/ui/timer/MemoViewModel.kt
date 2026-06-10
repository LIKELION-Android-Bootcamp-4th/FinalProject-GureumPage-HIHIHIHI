package com.hihihihi.presentation.ui.timer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hihihihi.domain.model.Quote
import com.hihihihi.domain.usecase.auth.GetCurrentUserIdUseCase
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
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase
) : ViewModel() {

    private val _ui = MutableStateFlow<MemoUiState>(MemoUiState.Content())
    val ui: StateFlow<MemoUiState> = _ui

    fun clear() {
        _ui.value = MemoUiState.Content()
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
        val uid = getCurrentUserIdUseCase()
        if (uid.isNullOrBlank()) {
            _ui.value = MemoUiState.Error(
                message = "AUTH_REQUIRED",
                previous = _ui.value as? MemoUiState.Content,
            )
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
                updateContent { it.copy(items = it.items + q) }
                onDone()
            } else {
                _ui.value = MemoUiState.Error(
                    message = r.exceptionOrNull()?.message ?: "메모 저장에 실패했습니다.",
                    previous = _ui.value as? MemoUiState.Content,
                )
            }
        }
    }

    private inline fun updateContent(
        crossinline transform: (MemoUiState.Content) -> MemoUiState.Content,
    ) {
        _ui.update { current -> transform(current.contentOrDefault()) }
    }
}
