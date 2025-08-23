package com.hihihihi.gureumpage.widgets.common

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.hihihihi.domain.model.ReadingStatus
import com.hihihihi.domain.usecase.daily.GetDailyReadPagesByUserIdAndDateUseCase
import com.hihihihi.domain.usecase.daily.GetDailyReadPagesUseCase
import com.hihihihi.domain.usecase.userbook.GetUserBooksByStatusUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable.invokeOnCompletion
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Calendar
import java.util.TimeZone
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WigetAutoUpdater @Inject constructor(
    @ApplicationContext private val appContext : Context,
    private val dispatcher: WidgetUpdateDispatcher,
    private val getUserBooksByStatus: GetUserBooksByStatusUseCase,
    private val getDailyReadPagesByUserIdAndDateUseCase: GetDailyReadPagesByUserIdAndDateUseCase,
    private val auth : FirebaseAuth
    ){
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var authJob: Job? = null
    private var userWatchJob: Job? = null
    private var tickJob: Job? = null

    fun start() {
        if (authJob != null) return
        authJob = scope.launch {
            rebindUserWatch(auth.currentUser?.uid)

            // Auth 상태 변화 대응
            val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
                rebindUserWatch(firebaseAuth.currentUser?.uid)
            }
            auth.addAuthStateListener(listener)

            // 이 Job이 취소될 때 리스너 정리
            invokeOnCompletion {
                auth.removeAuthStateListener(listener)
            }
        }
    }

    suspend fun stop() {
        authJob?.cancelAndJoin()
        authJob = null
        userWatchJob?.cancelAndJoin()
        userWatchJob = null
        tickJob?.cancelAndJoin()
        tickJob = null
    }

    private suspend fun periodicSafetyTick(intervalMillis: Long = 30 * 60 * 1000L) {
        while (scope.isActive) {
            delay(intervalMillis)
            dispatcher.updateAllWidgets()
        }
    }

    @OptIn(FlowPreview::class)
    @Synchronized
    private fun rebindUserWatch(uid: String?) {
        // 이전 Job 정리
        userWatchJob?.cancel()
        userWatchJob = null
        tickJob?.cancel()
        tickJob = null

        // 비로그인시
        if (uid.isNullOrBlank()) {
            // 비로그인: 최소한의 폴링만 (앱 내부 이벤트로 수동 갱신한다고 가정)
            tickJob = scope.launch { periodicSafetyTick() }
            return
        }

        // 로그인 된 경우: 유저 데이터 흐름에 반응
        userWatchJob = scope.launch {
            val currentBooksFlow = getUserBooksByStatus(
                userId = uid,
                status = ReadingStatus.READING
            )
            val dayOfStart = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)

                set(Calendar.DAY_OF_MONTH, 1)
                add(Calendar.MONTH, -1)
            }
            val dailyPagesFlow = getDailyReadPagesByUserIdAndDateUseCase(
                userId = uid,
                dayOfStart = dayOfStart.time
            )

            // 각각 하고 싶은 경우 아래 코드 처럼 각 위젯 업데이트 호출 해주면됨.
            // 읽고 있는책, 책들에 대한 Flow 감시
            currentBooksFlow
                .debounce(300)
                .distinctUntilChanged()
                .onEach {
                    dispatcher.updateCurrentReadingBooks()
                    dispatcher.updateCurrentReadingBook()
                }
                .launchIn(this)

            dailyPagesFlow
                .debounce(300)
                .distinctUntilChanged()
                .onEach { dispatcher.updateHistoryHeatMap() }
                .launchIn(this)

            tickJob = launch { periodicSafetyTick() }
        }
    }

}