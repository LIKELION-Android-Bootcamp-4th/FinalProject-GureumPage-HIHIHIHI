package com.hihihihi.domain.usecase.notification

import com.hihihihi.domain.model.NotificationSettings
import com.hihihihi.domain.repository.NotificationPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNotificationSettingsUseCase @Inject constructor(
    private val repository: NotificationPreferencesRepository,
) {
    operator fun invoke(): Flow<NotificationSettings> = repository.settings
}
