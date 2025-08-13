package com.hihihihi.data.local.datasource

import com.hihihihi.domain.model.GureumThemeType
import kotlinx.coroutines.flow.Flow

interface UserPreferencesLocalDataSource {
    val onboardingComplete: Flow<Boolean>
    val nickname: Flow<String>
    val theme: Flow<GureumThemeType>

    suspend fun setOnboardingComplete(complete: Boolean)
    suspend fun setNickname(nickname: String)
    suspend fun setTheme(theme: GureumThemeType)
}