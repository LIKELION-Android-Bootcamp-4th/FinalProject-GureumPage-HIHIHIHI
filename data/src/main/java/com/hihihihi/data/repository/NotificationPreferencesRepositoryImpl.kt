package com.hihihihi.data.repository

import com.hihihihi.data.local.datasource.NotificationPreferencesLocalDataSource
import com.hihihihi.domain.model.NotificationSettings
import com.hihihihi.domain.repository.NotificationPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NotificationPreferencesRepositoryImpl @Inject constructor(
    private val localDataSource: NotificationPreferencesLocalDataSource,
) : NotificationPreferencesRepository {
    override val settings: Flow<NotificationSettings> = localDataSource.settings
    override suspend fun updateSettings(settings: NotificationSettings) = localDataSource.updateSettings(settings)
}
