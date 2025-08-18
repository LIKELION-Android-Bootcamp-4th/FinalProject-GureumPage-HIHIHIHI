package com.hihihihi.gureumpage.ui.timer

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.hihihihi.domain.model.History
import com.hihihihi.domain.model.ReadingStatus
import com.hihihihi.domain.model.RecordType
import com.hihihihi.domain.usecase.history.AddHistoryUseCase
import com.hihihihi.domain.usecase.userbook.GetUserBooksByStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.LocalDateTime


@HiltViewModel
class TimerViewModel @Inject constructor(
    private val getUserBooksByStatus: GetUserBooksByStatusUseCase,
//    private val saveReadingTime: SaveReadingTimeUseCase
    private val addHistory: AddHistoryUseCase,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(TimerUiState())
    val uiState: StateFlow<TimerUiState> = _uiState

    //타이머
//    private var timerJob: Job? = null

    //스톱워치
    private var stopwatchJob: Job? = null

    private var booksJob: Job? = null

    private val currentUid: String?
        get() = auth.currentUser?.uid

    private var didShowCountdown: Boolean = false

    fun bind(userBookId: String) {
        val uid = currentUid ?: return
        booksJob?.cancel()
        didShowCountdown = false

        booksJob = viewModelScope.launch {
            // status == READING 전체를 구독하되, 해당 ID 한 권만 반영
            getUserBooksByStatus(uid, ReadingStatus.READING).collectLatest { books ->
                val pick = books.firstOrNull { it.userBookId == userBookId }
                Log.d("TimerVM", "bind => userBookId=$userBookId, pick=${pick?.title}")

                if (pick != null) {
                    _uiState.update {
                        it.copy(
                            bookTitle = pick.title,
                            author = pick.author,
                            bookImageUrl = pick.imageUrl,
                            startPage = pick.currentPage,
                            totalPage = pick.totalPage
                        )
                    }
                } else {
                    // 선택한 책을 찾지 못한 경우(상태 변경/삭제 등) 안전 초기화
                    _uiState.update {
                        it.copy(
                            bookTitle = "",
                            author = "",
                            bookImageUrl = "",
                            startPage = null,
                            totalPage = null
                        )
                    }
                }
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

    fun startWithCountdown() {
        if (stopwatchJob?.isActive == true) return

        viewModelScope.launch {
            for (i in 3 downTo 1) {
                Log.d("TimerVM", "countdown=$i")
                _uiState.update { it.copy(countdown = i, isRunning = false) }
                delay(1000)
            }
            Log.d("TimerVM", "countdown done")
            _uiState.update { it.copy(countdown = null) }
            startStopwatch()
        }
    }

    fun start() = startStopwatch()
    fun pause() = pauseStopwatch()

    private fun startStopwatch() {
        // 이미 실행 중이면 무시
        if (stopwatchJob?.isActive == true) return

        _uiState.update { it.copy(isRunning = true) }

        stopwatchJob = viewModelScope.launch {
            while (isActive) {
                delay(1_000L)
                _uiState.update { it.copy(elapsedSec = it.elapsedSec + 1) }
            }
        }
    }

    private fun pauseStopwatch() {
        _uiState.update { it.copy(isRunning = false) }
        stopwatchJob?.cancel()
        stopwatchJob = null
    }

    fun stop() {
        stopwatchJob?.cancel()
        stopwatchJob = null
        _uiState.update { it.copy(isRunning = false, elapsedSec = 0) }

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
            addHistory(history)
                .onSuccess {
                    _uiState.update { it.copy(elapsedSec = 0, isRunning = false) }
                }
                .onFailure { Log.e("TimerVM", "save failed", it) }
        }
    }

    override fun onCleared() {
//        timerJob?.cancel()
        stopwatchJob?.cancel()
        booksJob?.cancel()
        super.onCleared()
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

}