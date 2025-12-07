package com.hihihihi.data.repotisoryimpl

import com.hihihihi.data.local.datasource.UserPreferencesLocalDataSource
import com.hihihihi.data.remote.datasource.UserRemoteDataSource
import com.hihihihi.domain.model.GureumThemeType
import com.hihihihi.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserPreferencesRepositoryImpl @Inject constructor(
    private val localDatasource: UserPreferencesLocalDataSource,
    private val remoteDatasource: UserRemoteDataSource
) : UserPreferencesRepository {
    override val nickname: Flow<String> = localDatasource.nickname
    override val theme: Flow<GureumThemeType> = localDatasource.theme
    override val lastProvider: Flow<String> = localDatasource.lastProvider
    override val lastVisitFlow: Flow<Long> = localDatasource.lastVisitFlow

    override fun getOnboardingComplete(userId: String): Flow<Boolean> =
        localDatasource.getOnboardingComplete(userId)

    override suspend fun setOnboardingComplete(userId: String, complete: Boolean) {
        localDatasource.setOnboardingComplete(userId, complete)
    }

    override suspend fun setNickname(userId: String, nickname: String) {
        localDatasource.setNickname(nickname)
        remoteDatasource.updateNickname(userId, nickname)
    }

    override suspend fun setTheme(theme: GureumThemeType) {
        localDatasource.setTheme(theme)
    }

    override suspend fun setLastProvider(provider: String) {
        localDatasource.setLastProvider(provider)
    }

    override suspend fun clearAll() {
        localDatasource.clearAll()
    }

    override suspend fun updateLastVisit() {
        localDatasource.updateLastVisit()
    }
}