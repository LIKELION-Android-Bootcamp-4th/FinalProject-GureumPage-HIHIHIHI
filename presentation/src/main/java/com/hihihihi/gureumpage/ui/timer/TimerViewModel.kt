package com.hihihihi.gureumpage.ui.timer

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.hihihihi.domain.model.History
import com.hihihihi.domain.model.RecordType
import com.hihihihi.domain.usecase.history.AddHistoryUseCase
import com.hihihihi.domain.usecase.userbook.GetUserBookByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@HiltViewModel
class TimerViewModel @Inject constructor(
    private val getUserBook: GetUserBookByIdUseCase,
    private val addHistory: AddHistoryUseCase,
    private val auth: FirebaseAuth,
    private val timerRepository: TimerRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TimerUiState())
    val uiState: StateFlow<TimerUiState> = _uiState

    val sharedTimerState = timerRepository.timerState

    private var stopwatchJob: Job? = null
    private var booksJob: Job? = null

    private var didShowCountdown: Boolean = false

    init {
        viewModelScope.launch {
            timerRepository.timerState.collectLatest { shared ->
                _uiState.update {
                    it.copy(
                        elapsedSec = shared.elapsedSec,
                        isRunning = shared.isRunning,
                        bookTitle = shared.bookInfo?.title ?: "",
                        author = shared.bookInfo?.author ?: "",
                        bookImageUrl = shared.bookInfo?.imageUrl ?: "",
                    )
                }
            }
        }

        viewModelScope.launch {
            timerRepository.floatingActions.collectLatest { action ->
                handleFloatingAction(action)
            }
        }
    }

    private fun handleFloatingAction(action: FloatingAction) {
        when (action) {
            is FloatingAction.ToggleTimer -> {
                toggleRun()
            }

            is FloatingAction.OpenMemoDialog -> {
                _uiState.update { it.copy(showMemoDialog = true) }
            }

            is FloatingAction.ReturnToApp -> {
            }
        }
    }

    fun ensureFloatingWindowClosed(context: Context) {
        val intent = Intent(context, FloatingTimerService::class.java)
        context.stopService(intent)
    }

    fun showMemoDialog() {
        _uiState.update { it.copy(showMemoDialog = true) }
    }

    fun dismissMemoDialog() {
        _uiState.update { it.copy(showMemoDialog = false) }
    }

    fun bind(userBookId: String) {
        booksJob = viewModelScope.launch {
            val userBook = getUserBook(userBookId).first()

            _uiState.update {
                it.copy(
                    bookTitle = userBook.title,
                    author = userBook.author,
                    bookImageUrl = userBook.imageUrl,
                    startPage = userBook.currentPage,
                    totalPage = userBook.totalPage
                )
            }

            timerRepository.updateTimerState { shared ->
                shared.copy(
                    bookInfo = BookInfo(
                        title = userBook.title,
                        author = userBook.author,
                        imageUrl = userBook.imageUrl
                    ),
                    userBookId = userBook.userBookId,
                    startPage = userBook.currentPage,
                    totalPage = userBook.totalPage
                )
            }
        }
    }

    fun toggleRun() {
        val state = _uiState.value

        if (state.countdown != null) return

        if (state.isRunning) {
            pauseStopwatch()
        } else {
            if (!didShowCountdown) {
                didShowCountdown = true
                startWithCountdown()
            } else {
                startStopwatch()
            }
        }
    }

    private fun startWithCountdown() {
        if (stopwatchJob?.isActive == true) return

        viewModelScope.launch {
            for (i in 3 downTo 1) {
                _uiState.update { it.copy(countdown = i, isRunning = false) }
                delay(1000)
            }
            _uiState.update { it.copy(countdown = null) }
            startStopwatch()
        }
    }

    fun start() = startStopwatch()
    fun pause() = pauseStopwatch()

    private fun startStopwatch() {
        if (stopwatchJob?.isActive == true) return

        _uiState.update { it.copy(isRunning = true) }
        timerRepository.updateTimerState { it.copy(isRunning = true) }

        stopwatchJob = viewModelScope.launch {
            while (isActive) {
                delay(1_000L)
                val newSec = _uiState.value.elapsedSec + 1

                _uiState.update { it.copy(elapsedSec = newSec) }
                timerRepository.updateTimerState { it.copy(elapsedSec = newSec) }
            }
        }
    }

    private fun pauseStopwatch() {
        _uiState.update { it.copy(isRunning = false) }
        timerRepository.updateTimerState { it.copy(isRunning = false) }

        stopwatchJob?.cancel()
        stopwatchJob = null
    }

    fun stop() {
        stopwatchJob?.cancel()
        stopwatchJob = null

        _uiState.update { it.copy(isRunning = false, elapsedSec = 0) }
        timerRepository.updateTimerState { it.copy(isRunning = false, elapsedSec = 0) }
    }

    fun startFloatingWindowMode(context: Context) {
        val intent = Intent(context, FloatingTimerService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    fun stopFloatingWindowMode(context: Context) {
        val intent = Intent(context, FloatingTimerService::class.java)
        context.stopService(intent)
    }

    fun finishAndSave(userBookId: String?, startPage: Int, endPage: Int) {
        val uid = auth.currentUser?.uid ?: return
        val seconds = _uiState.value.elapsedSec
        val delta = (endPage - startPage).coerceAtLeast(0)

        val now = LocalDateTime.now()
        val history = History(
            id = "",
            userId = uid,
            userBookId = userBookId ?: return,
            date = now,
            startTime = now.minusSeconds(seconds.toLong()),
            endTime = now,
            readTime = seconds,
            readPageCount = delta,
            recordType = RecordType.TIMER
        )

        viewModelScope.launch {
            addHistory(history, endPage)
                .onSuccess {
                    _uiState.update { it.copy(elapsedSec = 0, isRunning = false) }
                    timerRepository.updateTimerState { it.copy(elapsedSec = 0, isRunning = false) }
                }
        }
    }

    override fun onCleared() {
        stopwatchJob?.cancel()
        booksJob?.cancel()
        super.onCleared()
    }

    fun syncWithFloatingWindow() {
        val currentState = _uiState.value
        timerRepository.updateTimerState { shared ->
            shared.copy(
                isRunning = currentState.isRunning,
                elapsedSec = currentState.elapsedSec,
                bookInfo = BookInfo(
                    title = currentState.bookTitle,
                    author = currentState.author,
                    imageUrl = currentState.bookImageUrl
                )
            )
        }
    }
}

//    private fun startTimer() {
//        // 이미 돌고 있으면 무시
//        if (timerJob?.isActive == true) return
//
//        _uiState.update { it.copy(isRunning = true) }
//
//        timerJob = viewModelScope.launch {
//            while (isActive) {
//                delay(1_000L) // 1초에 한 번씩 증가
//                val next = _uiState.value.elapsedSec + 1
//                val target = _uiState.value.targetSec
//
//                if (next >= target) {
//                    // 목표 도달: 진행을 멈추고 목표치로 고정
//                    _uiState.update { it.copy(elapsedSec = target, isRunning = false) }
//                    stopTimerInternal(cancelJobOnly = false)
//                    // TODO: 여기서 세션 저장(파이어스토어) 로직 연결
//                    break
//                } else {
//                    _uiState.update { it.copy(elapsedSec = next) }
//                }
//            }
//        }
//    }

//    private fun pauseTimer() {
//        _uiState.update { it.copy(isRunning = false) }
//        stopTimerInternal(cancelJobOnly = true)
//    }

//    fun stop() {
//        stopTimerInternal(cancelJobOnly = true)
//        _uiState.update { it.copy(isRunning = false, elapsedSec = 0) }
//        // TODO: 진행 중이던 세션을 저장/폐기할지 정책에 맞게 처리
//    }

//    /** 잡 취소 공통 처리 */
//    private fun stopTimerInternal(cancelJobOnly: Boolean) {
//        timerJob?.cancel()
//        timerJob = null
//        // cancelJobOnly=false일 땐 추가 동작이 필요하면 여기에
//    }