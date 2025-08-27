package com.hihihihi.gureumpage.ui.timer

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TimerRepository @Inject constructor() {
    private val repositoryScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val _timerState = MutableStateFlow(TimerSharedState())
    val timerState: StateFlow<TimerSharedState> = _timerState.asStateFlow()

    val isTimerRunning: StateFlow<Boolean> = timerState
        .map { it.isRunning }
        .distinctUntilChanged()
        .stateIn(
            scope = repositoryScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    private val _floatingActions = MutableSharedFlow<FloatingAction>(
        replay = 0,
        extraBufferCapacity = 1
    )
    val floatingActions: SharedFlow<FloatingAction> = _floatingActions.asSharedFlow()


    fun updateTimerState(update: (TimerSharedState) -> TimerSharedState) {
        _timerState.update(update)
    }

    suspend fun sendFloatingAction(action: FloatingAction) {
        _floatingActions.emit(action)
    }

    fun getTimerBookId(): String {
        val state = _timerState.value
        return state.userBookId
    }
}

data class TimerSharedState(
    val isRunning: Boolean = false,
    val elapsedSec: Int = 0,
    val bookInfo: BookInfo? = null,
    val userBookId: String = "",
    val startPage: Int? = null,
    val totalPage: Int? = null
)

data class BookInfo(
    val title: String,
    val author: String,
    val imageUrl: String,
)

sealed class FloatingAction {
    object OpenMemoDialog : FloatingAction()
    object ToggleTimer : FloatingAction()
    object ReturnToApp : FloatingAction()
}