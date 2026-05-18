package com.hihihihi.presentation.ui.notification

import androidx.compose.runtime.Immutable

@Immutable
data class NotificationSettingsUiState(
    val isDailyReminderEnabled: Boolean = false,
    val reminderHour: Int = 21,
    val reminderMinute: Int = 0,
    val isGoalAlertEnabled: Boolean = true,
    val isWeeklySummaryEnabled: Boolean = true,
    val isMonthlySummaryEnabled: Boolean = false,
    val isLoading: Boolean = true,
)
