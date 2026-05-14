package com.hihihihi.domain.usecase.notification

import com.hihihihi.domain.model.NotificationSettings
import com.hihihihi.domain.repository.NotificationPreferencesRepository
import javax.inject.Inject

class UpdateNotificationSettingsUseCase @Inject constructor(
    private val repository: NotificationPreferencesRepository,
) {
    suspend operator fun invoke(settings: NotificationSettings) = repository.updateSettings(settings)
}
