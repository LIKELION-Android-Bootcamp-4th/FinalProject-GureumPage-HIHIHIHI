package com.hihihihi.data.remote.datasource

import kotlinx.coroutines.flow.Flow

interface ThemeRemoteDataSource {
    fun isDarkTheme(): Flow<Boolean>
    suspend fun setDarkTheme(enabled: Boolean)
}