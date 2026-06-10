package com.hihihihi.domain.repository

import com.hihihihi.domain.model.NotificationSettings
import kotlinx.coroutines.flow.Flow

interface NotificationPreferencesRepository {
    val settings: Flow<NotificationSettings>
    suspend fun updateSettings(settings: NotificationSettings)
}
