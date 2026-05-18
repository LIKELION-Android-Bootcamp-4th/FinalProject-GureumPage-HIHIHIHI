package com.hihihihi.presentation.ui.notification

import android.content.Context
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
import javax.inject.Inject

@HiltViewModel
class NotificationSettingsViewModel @Inject constructor(
    private val getNotificationSettingsUseCase: GetNotificationSettingsUseCase,
    private val updateNotificationSettingsUseCase: UpdateNotificationSettingsUseCase,
    @param:ApplicationContext private val context: Context,
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationSettingsUiState())
    val uiState: StateFlow<NotificationSettingsUiState> = _uiState

    init {
        viewModelScope.launch {
            getNotificationSettingsUseCase()
                .catch { _uiState.update { it.copy(isLoading = false) } }
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

    fun setDailyReminderEnabled(enabled: Boolean) {
        val updated = _uiState.value.copy(isDailyReminderEnabled = enabled)
        _uiState.update { updated }
        viewModelScope.launch {
            save(updated)
            if (enabled) {
                ReminderScheduler.scheduleDaily(context, updated.reminderHour, updated.reminderMinute)
            } else {
                ReminderScheduler.cancel(context)
            }
        }
    }

    fun setReminderTime(hour: Int, minute: Int) {
        val updated = _uiState.value.copy(reminderHour = hour, reminderMinute = minute)
        _uiState.update { updated }
        viewModelScope.launch {
            save(updated)
            if (updated.isDailyReminderEnabled) {
                ReminderScheduler.scheduleDaily(context, hour, minute)
            }
        }
    }

    fun setGoalAlertEnabled(enabled: Boolean) {
        val updated = _uiState.value.copy(isGoalAlertEnabled = enabled)
        _uiState.update { updated }
        viewModelScope.launch {
            save(updated)
            if (!enabled) Goal80ReminderScheduler.cancelToday(context)
        }
    }

    fun setWeeklySummaryEnabled(enabled: Boolean) {
        val updated = _uiState.value.copy(isWeeklySummaryEnabled = enabled)
        _uiState.update { updated }
        viewModelScope.launch {
            save(updated)
            if (enabled) {
                SummaryScheduler.scheduleWeekly(context)
            } else {
                SummaryScheduler.cancelWeekly(context)
            }
        }
    }

    fun setMonthlySummaryEnabled(enabled: Boolean) {
        val updated = _uiState.value.copy(isMonthlySummaryEnabled = enabled)
        _uiState.update { updated }
        viewModelScope.launch {
            save(updated)
            if (enabled) SummaryScheduler.scheduleMonthly(context)
            else SummaryScheduler.cancelMonthly(context)
        }
    }

    private suspend fun save(state: NotificationSettingsUiState) {
        updateNotificationSettingsUseCase(
            NotificationSettings(
                isDailyReminderEnabled = state.isDailyReminderEnabled,
                reminderHour = state.reminderHour,
                reminderMinute = state.reminderMinute,
                isGoalAlertEnabled = state.isGoalAlertEnabled,
                isWeeklySummaryEnabled = state.isWeeklySummaryEnabled,
                isMonthlySummaryEnabled = state.isMonthlySummaryEnabled,
            )
        )
    }
}
