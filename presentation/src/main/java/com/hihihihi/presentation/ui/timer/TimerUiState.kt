package com.hihihihi.presentation.ui.timer

import androidx.compose.runtime.Immutable

@Immutable
sealed interface TimerUiState {
    @Immutable
    data object Loading : TimerUiState

    @Immutable
    data class Content(
        val bookTitle: String = "",
        val author: String = "",
        val bookImageUrl: String = "",
        val elapsedSec: Int = 0,
        val targetSec: Int = 60 * 30,
        val isRunning: Boolean = false,
        val memoLines: List<String> = emptyList(),
        val ringPeriodSec: Int = 1800,
        val startPage: Int? = null,
        val totalPage: Int? = null,
        val countdown: Int? = null,
        val dialogState: TimerDialogState = TimerDialogState.None,
    ) : TimerUiState {
        val showMemoDialog: Boolean get() = dialogState is TimerDialogState.Memo
        val showStopDialog: Boolean get() = dialogState is TimerDialogState.StopConfirm
        val showBackExitScreen: Boolean get() = dialogState is TimerDialogState.BackExit
        val progress: Float
            get() {
                val period = ringPeriodSec.coerceAtLeast(1)
                return ((elapsedSec % period).toFloat() / period).coerceIn(0f, 1f)
            }
        val displayTimeMMSS: String
            get() {
                val h = elapsedSec / 3600
                val m = (elapsedSec % 3600) / 60
                val s = elapsedSec % 60
                return if (h > 0) "%d:%02d:%02d".format(h, m, s)
                else "%02d:%02d".format(m, s)
            }
    }

    @Immutable
    data class Error(
        val message: String,
        val previous: Content? = null,
    ) : TimerUiState
}

internal fun TimerUiState.contentOrDefault(): TimerUiState.Content = when (this) {
    is TimerUiState.Content -> this
    is TimerUiState.Error -> previous ?: TimerUiState.Content()
    TimerUiState.Loading -> TimerUiState.Content()
}
