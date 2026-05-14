package com.hihihihi.data.local.datasource

import com.hihihihi.domain.model.NotificationSettings
import kotlinx.coroutines.flow.Flow

interface NotificationPreferencesLocalDataSource {
    val settings: Flow<NotificationSettings>
    suspend fun updateSettings(settings: NotificationSettings)
}
