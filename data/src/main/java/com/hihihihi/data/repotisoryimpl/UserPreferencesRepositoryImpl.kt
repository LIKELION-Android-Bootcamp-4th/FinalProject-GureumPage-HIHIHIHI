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
    override val onboardingComplete: Flow<Boolean> = localDatasource.onboardingComplete
    override val nickname: Flow<String> = localDatasource.nickname
    override val theme: Flow<GureumThemeType> = localDatasource.theme

    override suspend fun setOnboardingComplete(complete: Boolean) {
        localDatasource.setOnboardingComplete(complete)
    }

    override suspend fun setNickname(userId: String, nickname: String) {
        localDatasource.setNickname(nickname)
        remoteDatasource.updateNickname(userId, nickname)
    }

    override suspend fun setTheme(theme: GureumThemeType) {
        localDatasource.setTheme(theme)
    }
}