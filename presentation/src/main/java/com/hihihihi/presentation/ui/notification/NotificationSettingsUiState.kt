package com.hihihihi.presentation.ui.notification

import androidx.compose.runtime.Immutable

@Immutable
sealed interface NotificationSettingsUiState {
    @Immutable
    data object Loading : NotificationSettingsUiState

    @Immutable
    data class Content(
        val isDailyReminderEnabled: Boolean = false,
        val reminderHour: Int = 21,
        val reminderMinute: Int = 0,
        val isGoalAlertEnabled: Boolean = true,
        val isWeeklySummaryEnabled: Boolean = true,
        val isMonthlySummaryEnabled: Boolean = false,
    ) : NotificationSettingsUiState

    @Immutable
    data class Error(
        val message: String,
        val previous: Content? = null,
    ) : NotificationSettingsUiState
}

internal fun NotificationSettingsUiState.contentOrDefault(): NotificationSettingsUiState.Content = when (this) {
    is NotificationSettingsUiState.Content -> this
    is NotificationSettingsUiState.Error -> previous ?: NotificationSettingsUiState.Content()
    NotificationSettingsUiState.Loading -> NotificationSettingsUiState.Content()
}
