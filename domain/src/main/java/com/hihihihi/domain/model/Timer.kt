package com.hihihihi.domain.model

data class TimerState (
    val isRunning: Boolean = false,
    val elapsedSec: Int = 0,
    val bookInfo: BookInfo? = null,
    val userBookId: String ="",
    val startPage: Int? = null,
    val totalPage: Int? = null
)

data class BookInfo(
    val title: String,
    val author: String,
    val imageUrl: String
)

sealed class FloatingAction {
    object OpenMemoDialog : FloatingAction()
    object ToggleTimer : FloatingAction()
    object ReturnToApp : FloatingAction()
}