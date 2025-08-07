package com.hihihihi.data.remote.datasourceimpl

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.hihihihi.data.remote.datasource.ThemeRemoteDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

//DataStore를 통해 다크모드 설정을 실제로 저장하고 불러오는 구현체
private val Context.dataStore by preferencesDataStore(name = "theme_settings")

class ThemeRemoteDataSourceImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ThemeRemoteDataSource {
    private val THEME_KEY = booleanPreferencesKey("dark_theme")

    override fun isDarkTheme(): Flow<Boolean> =
        context.dataStore.data.map { it[THEME_KEY] ?: false }

    override suspend fun setDarkTheme(enabled: Boolean) {
        context.dataStore.edit { it[THEME_KEY] = enabled }
    }
}