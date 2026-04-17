package com.hihihihi.presentation.ui.timer

sealed interface TimerDialogState {
    data object None : TimerDialogState
    data object Memo : TimerDialogState
    data class StopConfirm(val wasRunning: Boolean) : TimerDialogState
    data class BackExit(val wasRunning: Boolean) : TimerDialogState
}

enum class TimerDialogType { Memo, StopConfirm, BackExit }

sealed interface TimerEffect {
    data object RequestOverlayPermission : TimerEffect
    data object StartFloatingWindow : TimerEffect
}
