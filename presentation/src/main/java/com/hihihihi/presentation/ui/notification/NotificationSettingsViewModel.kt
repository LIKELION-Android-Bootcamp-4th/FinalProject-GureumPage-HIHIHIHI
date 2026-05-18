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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
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

    private val _uiState = MutableStateFlow(NotificationSettingsUiState())
    val uiState: StateFlow<NotificationSettingsUiState> = _uiState
    private val settingsUpdateMutex = Mutex()

    init {
        viewModelScope.launch {
            getNotificationSettingsUseCase()
                .catch { exception ->
                    Log.e(TAG, "알림 설정 조회 실패", exception)
                    _uiState.update { it.copy(isLoading = false) }
                }
                .collect { settings ->
                    _uiState.update {
                        it.copy(
                            isDailyReminderEnabled = settings.isDailyReminderEnabled,
                            reminderHour = settings.reminderHour,
                            reminderMinute = settings.reminderMinute,
                            isGoalAlertEnabled = settings.isGoalAlertEnabled,
                            isWeeklySummaryEnabled = settings.isWeeklySummaryEnabled,
                            isMonthlySummaryEnabled = settings.isMonthlySummaryEnabled,
                            isLoading = false,
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
        transform: (NotificationSettingsUiState) -> NotificationSettingsUiState,
        afterSaved: suspend (NotificationSettingsUiState) -> Unit = {},
    ) {
        viewModelScope.launch {
            settingsUpdateMutex.withLock {
                val previous = _uiState.value
                val updated = transform(previous)
                _uiState.update { updated }

                if (save(updated).isSuccess) {
                    afterSaved(updated)
                } else {
                    _uiState.update { previous }
                }
            }
        }
    }

    private suspend fun save(state: NotificationSettingsUiState): Result<Unit> {
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
