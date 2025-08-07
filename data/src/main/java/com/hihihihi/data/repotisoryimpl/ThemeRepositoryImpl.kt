package com.hihihihi.data.repotisoryimpl

import com.hihihihi.data.remote.datasource.ThemeRemoteDataSource
import com.hihihihi.domain.repository.ThemeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ThemeRepositoryImpl @Inject constructor(
    private val themeRemoteDatasource: ThemeRemoteDataSource
) : ThemeRepository {
    override fun isDarkTheme(): Flow<Boolean> = themeRemoteDatasource.isDarkTheme()
    override suspend fun setDarkTheme(enabled: Boolean) = themeRemoteDatasource.setDarkTheme(enabled)
}