package com.hihihihi.presentation.ui.timer

sealed class TimerDialogState {
    object None : TimerDialogState()
    object Memo : TimerDialogState()
    data class StopConfirm(val wasRunning: Boolean) : TimerDialogState()
    data class BackExit(val wasRunning: Boolean) : TimerDialogState()
}

enum class TimerDialogType { Memo, StopConfirm, BackExit }

sealed class TimerEffect {
    object RequestOverlayPermission : TimerEffect()
    object StartFloatingWindow : TimerEffect()
}