package com.hihihihi.domain.repository

import com.hihihihi.domain.model.GureumThemeType
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val nickname: Flow<String>
    val theme: Flow<GureumThemeType>
    val lastProvider: Flow<String>
    val lastVisitFlow: Flow<Long>

    fun getOnboardingComplete(userId: String): Flow<Boolean>

    suspend fun setOnboardingComplete(userId: String, complete: Boolean)
    suspend fun setNickname(userId: String, nickname: String)
    suspend fun setTheme(theme: GureumThemeType)
    suspend fun setLastProvider(provider: String)
    suspend fun clearAll()
    suspend fun updateLastVisit()
}