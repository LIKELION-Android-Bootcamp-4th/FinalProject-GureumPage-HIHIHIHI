package com.hihihihi.data.local.datasourceimpl

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.hihihihi.data.local.datasource.NotificationPreferencesLocalDataSource
import com.hihihihi.domain.model.NotificationSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

private val Context.notificationDataStore: DataStore<Preferences> by preferencesDataStore(name = "notification_prefs")

private object NotifPrefKeys {
    val DAILY_REMINDER_ENABLED = booleanPreferencesKey("daily_reminder_enabled")
    val REMINDER_HOUR = intPreferencesKey("reminder_hour")
    val REMINDER_MINUTE = intPreferencesKey("reminder_minute")
    val GOAL_ALERT_ENABLED = booleanPreferencesKey("goal_alert_enabled")
    val WEEKLY_SUMMARY_ENABLED = booleanPreferencesKey("weekly_summary_enabled")
    val MONTHLY_SUMMARY_ENABLED = booleanPreferencesKey("monthly_summary_enabled")
}

class NotificationPreferencesLocalDataSourceImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : NotificationPreferencesLocalDataSource {

    override val settings: Flow<NotificationSettings> =
        context.notificationDataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map {
                NotificationSettings(
                    isDailyReminderEnabled = it[NotifPrefKeys.DAILY_REMINDER_ENABLED] ?: false,
                    reminderHour = it[NotifPrefKeys.REMINDER_HOUR] ?: 21,
                    reminderMinute = it[NotifPrefKeys.REMINDER_MINUTE] ?: 0,
                    isGoalAlertEnabled = it[NotifPrefKeys.GOAL_ALERT_ENABLED] ?: true,
                    isWeeklySummaryEnabled = it[NotifPrefKeys.WEEKLY_SUMMARY_ENABLED] ?: true,
                    isMonthlySummaryEnabled = it[NotifPrefKeys.MONTHLY_SUMMARY_ENABLED] ?: false,
                )
            }

    override suspend fun updateSettings(settings: NotificationSettings) {
        context.notificationDataStore.edit {
            it[NotifPrefKeys.DAILY_REMINDER_ENABLED] = settings.isDailyReminderEnabled
            it[NotifPrefKeys.REMINDER_HOUR] = settings.reminderHour
            it[NotifPrefKeys.REMINDER_MINUTE] = settings.reminderMinute
            it[NotifPrefKeys.GOAL_ALERT_ENABLED] = settings.isGoalAlertEnabled
            it[NotifPrefKeys.WEEKLY_SUMMARY_ENABLED] = settings.isWeeklySummaryEnabled
            it[NotifPrefKeys.MONTHLY_SUMMARY_ENABLED] = settings.isMonthlySummaryEnabled
        }
    }
}
