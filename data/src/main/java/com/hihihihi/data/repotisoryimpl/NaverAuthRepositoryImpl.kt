package com.hihihihi.data.repotisoryimpl

import android.app.Activity
import com.hihihihi.data.remote.datasource.NaverDataSource
import com.hihihihi.domain.repository.NaverAuthRepository
import javax.inject.Inject

class NaverAuthRepositoryImpl @Inject constructor(
    private val naverDataSource: NaverDataSource
) : NaverAuthRepository {
    override suspend fun login(activity: Activity): String {
        return naverDataSource.signIn(activity)
    }
}