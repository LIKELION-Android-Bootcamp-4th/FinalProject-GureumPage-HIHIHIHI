package com.hihihihi.gureumpage.ui.mypage

data class MyPageUiState(
    val loading: Boolean = true,
    val error: String? = null,
    val nickname: String = "",
    val appellation: String = "",
    val totalPages: Int = 0,
    val totalBooks: Int = 0,
    val totalReadMinutes: Int = 0,
) {
}