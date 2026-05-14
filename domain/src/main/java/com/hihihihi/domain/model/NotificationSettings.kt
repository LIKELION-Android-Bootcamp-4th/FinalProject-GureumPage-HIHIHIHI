package com.hihihihi.domain.model

data class NotificationSettings(
    val isDailyReminderEnabled: Boolean = false,
    val reminderHour: Int = 21,
    val reminderMinute: Int = 0,
    val isGoalAlertEnabled: Boolean = true,
    val isWeeklySummaryEnabled: Boolean = true,
)
