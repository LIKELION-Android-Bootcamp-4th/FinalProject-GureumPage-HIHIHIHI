package com.hihihihi.data.local.datasource

import com.hihihihi.domain.model.GureumThemeType
import kotlinx.coroutines.flow.Flow

interface UserPreferencesLocalDataSource {
    val nickname: Flow<String>
    val theme: Flow<GureumThemeType>

    fun getOnboardingComplete(userId: String): Flow<Boolean>

    suspend fun setOnboardingComplete(userId: String, complete: Boolean)
    suspend fun setNickname(nickname: String)
    suspend fun setTheme(theme: GureumThemeType)
    suspend fun clearAll()
}