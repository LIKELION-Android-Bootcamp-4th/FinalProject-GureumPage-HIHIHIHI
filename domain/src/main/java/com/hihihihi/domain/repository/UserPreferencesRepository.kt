package com.hihihihi.domain.repository

import com.hihihihi.domain.model.GureumThemeType
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val onboardingComplete: Flow<Boolean>
    val nickname: Flow<String>
    val theme: Flow<GureumThemeType>

    suspend fun setOnboardingComplete(complete: Boolean)
    suspend fun setNickname(nickname: String)
    suspend fun setTheme(theme: GureumThemeType)
}