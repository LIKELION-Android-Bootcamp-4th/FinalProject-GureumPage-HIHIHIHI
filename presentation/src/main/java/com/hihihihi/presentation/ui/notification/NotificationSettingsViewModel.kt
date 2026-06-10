package com.hihihihi.presentation.ui.notification

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hihihihi.domain.model.NotificationSettings
import com.hihihihi.domain.usecase.notification.GetNotificationSettingsUseCase
import com.hihihihi.domain.usecase.notification.UpdateNotificationSettingsUseCase
import com.hihihihi.presentation.notification.progress.Goal80ReminderScheduler
import com.hihihihi.presentation.notification.reminder.ReminderScheduler
import com.hihihihi.presentation.notification.summary.SummaryScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

@HiltViewModel
class NotificationSettingsViewModel @Inject constructor(
    private val getNotificationSettingsUseCase: GetNotificationSettingsUseCase,
    private val updateNotificationSettingsUseCase: UpdateNotificationSettingsUseCase,
    @param:ApplicationContext private val context: Context,
) : ViewModel() {

    private val _uiState = MutableStateFlow<NotificationSettingsUiState>(NotificationSettingsUiState.Loading)
    val uiState: StateFlow<NotificationSettingsUiState> = _uiState

    private val _effect = Channel<NotificationSettingsEffect>(Channel.BUFFERED)
    val effect: Flow<NotificationSettingsEffect> = _effect.receiveAsFlow()

    private val settingsUpdateMutex = Mutex()

    init {
        viewModelScope.launch {
            getNotificationSettingsUseCase()
                .catch { exception ->
                    Log.e(TAG, "알림 설정 조회 실패", exception)
                    _uiState.update { current ->
                        NotificationSettingsUiState.Error(
                            message = "알림 설정을 불러오지 못했습니다.",
                            previous = current as? NotificationSettingsUiState.Content,
                        )
                    }
                    _effect.send(NotificationSettingsEffect.ShowMessage("알림 설정을 불러오지 못했습니다."))
                }
                .collect { settings ->
                    _uiState.update {
                        NotificationSettingsUiState.Content(
                            isDailyReminderEnabled = settings.isDailyReminderEnabled,
                            reminderHour = settings.reminderHour,
                            reminderMinute = settings.reminderMinute,
                            isGoalAlertEnabled = settings.isGoalAlertEnabled,
                            isWeeklySummaryEnabled = settings.isWeeklySummaryEnabled,
                            isMonthlySummaryEnabled = settings.isMonthlySummaryEnabled,
                        )
                    }
                }
        }
    }

    fun setDailyReminderEnabled(enabled: Boolean) =
        updateAndPersist(
            transform = { it.copy(isDailyReminderEnabled = enabled) },
            afterSaved = { updated ->
                if (updated.isDailyReminderEnabled) {
                    ReminderScheduler.scheduleDaily(context, updated.reminderHour, updated.reminderMinute)
                } else {
                    ReminderScheduler.cancel(context)
                }
            },
        )

    fun setReminderTime(hour: Int, minute: Int) =
        updateAndPersist(
            transform = { it.copy(reminderHour = hour, reminderMinute = minute) },
            afterSaved = { updated ->
                if (updated.isDailyReminderEnabled) {
                    ReminderScheduler.scheduleDaily(context, hour, minute)
                }
            },
        )

    fun setGoalAlertEnabled(enabled: Boolean) =
        updateAndPersist(
            transform = { it.copy(isGoalAlertEnabled = enabled) },
            afterSaved = { updated ->
                if (!updated.isGoalAlertEnabled) Goal80ReminderScheduler.cancelToday(context)
            },
        )

    fun setWeeklySummaryEnabled(enabled: Boolean) =
        updateAndPersist(
            transform = { it.copy(isWeeklySummaryEnabled = enabled) },
            afterSaved = { updated ->
                if (updated.isWeeklySummaryEnabled) {
                    SummaryScheduler.scheduleWeekly(context)
                } else {
                    SummaryScheduler.cancelWeekly(context)
                }
            },
        )

    fun setMonthlySummaryEnabled(enabled: Boolean) =
        updateAndPersist(
            transform = { it.copy(isMonthlySummaryEnabled = enabled) },
            afterSaved = { updated ->
                if (updated.isMonthlySummaryEnabled) {
                    SummaryScheduler.scheduleMonthly(context)
                } else {
                    SummaryScheduler.cancelMonthly(context)
                }
            },
        )

    private fun updateAndPersist(
        transform: (NotificationSettingsUiState.Content) -> NotificationSettingsUiState.Content,
        afterSaved: suspend (NotificationSettingsUiState.Content) -> Unit = {},
    ) {
        viewModelScope.launch {
            settingsUpdateMutex.withLock {
                val previous = _uiState.value.contentOrDefault()
                val updated = transform(previous)
                _uiState.update { updated }

                if (save(updated).isSuccess) {
                    afterSaved(updated)
                } else {
                    _uiState.update { previous }
                    _effect.send(NotificationSettingsEffect.ShowMessage("알림 설정을 저장하지 못했습니다."))
                }
            }
        }
    }

    private suspend fun save(state: NotificationSettingsUiState.Content): Result<Unit> {
        val settings = runCatching {
            NotificationSettings(
                isDailyReminderEnabled = state.isDailyReminderEnabled,
                reminderHour = state.reminderHour,
                reminderMinute = state.reminderMinute,
                isGoalAlertEnabled = state.isGoalAlertEnabled,
                isWeeklySummaryEnabled = state.isWeeklySummaryEnabled,
                isMonthlySummaryEnabled = state.isMonthlySummaryEnabled,
            )
        }.getOrElse { exception ->
            Log.e(TAG, "알림 설정 저장 실패", exception)
            return Result.failure(exception)
        }

        return updateNotificationSettingsUseCase(settings).onFailure { exception ->
            Log.e(TAG, "알림 설정 저장 실패", exception)
        }
    }

    private companion object {
        const val TAG = "NotificationSettingsVM"
    }
}

sealed interface NotificationSettingsEffect {
    data class ShowMessage(val message: String) : NotificationSettingsEffect
}
