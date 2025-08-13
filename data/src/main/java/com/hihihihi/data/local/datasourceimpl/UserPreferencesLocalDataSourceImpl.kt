package com.hihihihi.data.local.datasourceimpl

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.hihihihi.data.local.datasource.UserPreferencesLocalDataSource
import com.hihihihi.domain.model.GureumThemeType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.userPrefsDataStore by preferencesDataStore(name = "user_prefs")

object PrefKeys {
    val ONBOARDING_COMPLETE = booleanPreferencesKey("onboarding_complete")
    val NICKNAME = stringPreferencesKey("nickname")
    val THEME = stringPreferencesKey("theme")
}

class UserPreferencesLocalDataSourceImpl @Inject constructor(
    private val context: Context
) : UserPreferencesLocalDataSource {
    override val onboardingComplete: Flow<Boolean> =
        context.userPrefsDataStore.data.map { it[PrefKeys.ONBOARDING_COMPLETE] ?: false }

    override val nickname: Flow<String> =
        context.userPrefsDataStore.data.map { it[PrefKeys.NICKNAME] ?: "" }

    override val theme: Flow<GureumThemeType> =
        context.userPrefsDataStore.data.map {
            when (it[PrefKeys.THEME]) {
                GureumThemeType.LIGHT.name -> GureumThemeType.LIGHT
                GureumThemeType.DARK.name -> GureumThemeType.DARK
                else -> GureumThemeType.DARK
            }
        }

    override suspend fun setOnboardingComplete(complete: Boolean) {
        context.userPrefsDataStore.edit { it[PrefKeys.ONBOARDING_COMPLETE] = complete }
    }

    override suspend fun setNickname(nickname: String) {
        context.userPrefsDataStore.edit { it[PrefKeys.NICKNAME] = nickname }
    }

    override suspend fun setTheme(theme: GureumThemeType) {
        context.userPrefsDataStore.edit { it[PrefKeys.THEME] = theme.name }
    }
}
