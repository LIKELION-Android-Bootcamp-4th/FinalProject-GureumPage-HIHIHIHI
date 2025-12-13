package com.hihihihi.data.local.datasourceimpl

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.hihihihi.data.local.datasource.UserPreferencesLocalDataSource
import com.hihihihi.data.local.datasourceimpl.PrefKeys.LAST_VISIT
import com.hihihihi.domain.model.GureumThemeType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

object PrefKeys {
    val NICKNAME = stringPreferencesKey("nickname")
    val THEME = stringPreferencesKey("theme")
    val LAST_PROVIDER = stringPreferencesKey("last_provider")
    val LAST_VISIT = longPreferencesKey("last_visit")

    fun getOnboardingCompleteKey(userId: String) = booleanPreferencesKey("onboarding_complete_$userId")
}

class UserPreferencesLocalDataSourceImpl @Inject constructor(
    private val context: Context
) : UserPreferencesLocalDataSource {
    override val nickname: Flow<String> =
        context.userDataStore.data.map { it[PrefKeys.NICKNAME] ?: "" }

    override val theme: Flow<GureumThemeType> =
        context.userDataStore.data.map {
            when (it[PrefKeys.THEME]) {
                GureumThemeType.LIGHT.name -> GureumThemeType.LIGHT
                GureumThemeType.DARK.name -> GureumThemeType.DARK
                else -> GureumThemeType.DARK
            }
        }

    override val lastProvider: Flow<String> =
        context.userDataStore.data.map { it[PrefKeys.LAST_PROVIDER] ?: "" }

    override fun getOnboardingComplete(userId: String): Flow<Boolean> =
        context.userDataStore.data.map {
            it[PrefKeys.getOnboardingCompleteKey(userId)] ?: false
        }

    override suspend fun setOnboardingComplete(userId: String, complete: Boolean) {
        context.userDataStore.edit { it[PrefKeys.getOnboardingCompleteKey(userId)] = complete }
    }

    override suspend fun setNickname(nickname: String) {
        context.userDataStore.edit { it[PrefKeys.NICKNAME] = nickname }
    }

    override suspend fun setTheme(theme: GureumThemeType) {
        context.userDataStore.edit { it[PrefKeys.THEME] = theme.name }
    }

    override suspend fun setLastProvider(provider: String) {
        context.userDataStore.edit { it[PrefKeys.LAST_PROVIDER] = provider }
    }

    override suspend fun clearAll() {
        context.userDataStore.edit { it.clear() }
    }

    override val lastVisitFlow: Flow<Long> =
        context.userDataStore.data.map { it[LAST_VISIT] ?: 0L }

    override suspend fun updateLastVisit() {
        val now = System.currentTimeMillis()
        context.userDataStore.edit { it[LAST_VISIT] = now }
    }
}
