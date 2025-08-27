package com.hihihihi.gureumpage.ui.mypage

import com.hihihihi.domain.usecase.user.MyPageData

data class MyPageUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val myPageData: MyPageData? = null
)