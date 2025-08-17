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
import java.time.LocalDate
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

    init {
        // 로그인 현재 uid 사용
//        val uid = FirebaseAuth.getInstance().currentUser?.uid
//        if (uid != null) observeNowReading(uid)
        val uid = "iK4v1WW1ZX4gID2HueBi"
        observeNowReading(uid)
    }

    private fun observeNowReading(uid: String) = viewModelScope.launch {
        // Firestore Flow 구독: status == READING
        getUserBooksByStatus(uid, ReadingStatus.READING).collectLatest { books ->
            // 최신 책 하나 고르기 (createdAt 기준 내림차순)
            val pick = books.maxByOrNull { it.createdAt ?: LocalDateTime.MIN }

            Log.d("TimerVM", "nowReading => title=${pick?.title}, author=${pick?.author}, image=${pick?.imageUrl}")


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
            }
        }
    }

    fun toggleRun() {
        if (_uiState.value.isRunning) {
            pauseStopwatch()
        } else {
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
        // TODO: 필요 시 중단 시점 기록/저장 로직 연결
    }

    fun finishAndSave(userBookId: String?, startPage: Int, endPage: Int) {
        val uid = auth.currentUser?.uid ?: "iK4v1WW1ZX4gID2HueBi"
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