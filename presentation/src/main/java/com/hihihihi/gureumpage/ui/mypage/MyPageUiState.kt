package com.hihihihi.gureumpage.ui.mypage

data class MyPageUiState(
    val loading: Boolean = true,
    val error: String? = null,
    val nickname: String = "",
    val appellation: String = "",
    val dailyGoalTime: Int = 0
) {
}