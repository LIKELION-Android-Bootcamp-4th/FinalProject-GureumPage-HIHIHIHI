package com.hihihihi.presentation.ui.mypage

import com.hihihihi.presentation.ui.model.MyPageUiModel

data class MyPageUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val myPageUiModel: MyPageUiModel? = null,
    val dialogState: MyPageDialogState = MyPageDialogState.None,
)
