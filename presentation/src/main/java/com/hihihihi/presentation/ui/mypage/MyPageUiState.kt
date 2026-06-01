package com.hihihihi.presentation.ui.mypage

import androidx.compose.runtime.Immutable
import com.hihihihi.presentation.ui.model.MyPageUiModel

@Immutable
sealed interface MyPageUiState {
    @Immutable
    data object Loading : MyPageUiState

    @Immutable
    data class Content(
        val myPageUiModel: MyPageUiModel? = null,
        val dialogState: MyPageDialogState = MyPageDialogState.None,
    ) : MyPageUiState

    @Immutable
    data class Error(
        val message: String,
        val previous: Content? = null,
    ) : MyPageUiState
}

internal fun MyPageUiState.contentOrDefault(): MyPageUiState.Content = when (this) {
    is MyPageUiState.Content -> this
    is MyPageUiState.Error -> previous ?: MyPageUiState.Content()
    MyPageUiState.Loading -> MyPageUiState.Content()
}
