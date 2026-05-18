package com.hihihihi.domain.model

data class NotificationSettings(
    val isDailyReminderEnabled: Boolean = false,
    val reminderHour: Int = 21,
    val reminderMinute: Int = 0,
    val isGoalAlertEnabled: Boolean = true,
    val isWeeklySummaryEnabled: Boolean = true,
    val isMonthlySummaryEnabled: Boolean = false,
) {
    init {
        require(reminderHour in 0..23) { "reminderHour must be between 0 and 23" }
        require(reminderMinute in 0..59) { "reminderMinute must be between 0 and 59" }
    }
}
