package com.hihihihi.data.local.datasource

import com.hihihihi.domain.model.GureumThemeType
import kotlinx.coroutines.flow.Flow

interface UserPreferencesLocalDataSource {
    val nickname: Flow<String>
    val theme: Flow<GureumThemeType>
    val lastProvider: Flow<String>
    // 추후 마지막 방문일이 필요할 때 사용
    val lastVisitFlow: Flow<Long>

    fun getOnboardingComplete(userId: String): Flow<Boolean>

    suspend fun setOnboardingComplete(userId: String, complete: Boolean)
    suspend fun setNickname(nickname: String)
    suspend fun setTheme(theme: GureumThemeType)
    suspend fun setLastProvider(provider: String)
    suspend fun clearAll()
    suspend fun updateLastVisit()
}