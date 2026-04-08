package com.hihihihi.data.repository

import com.hihihihi.data.remote.datasource.AuthDataSource
import com.hihihihi.domain.repository.AuthRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: AuthDataSource,
) : AuthRepository {

    override suspend fun googleLogin(idToken: String) {
        authDataSource.signInWithGoogleCredential(idToken).await()
    }

    override suspend fun signInWithCustomToken(functionName: String, accessToken: String) {
        val result = authDataSource.requestCustomToken(functionName, accessToken).await()
        val customToken = (result.data as Map<*, *>)["token"] as String
        authDataSource.signInWithCustomToken(customToken).await()
    }

    override suspend fun kakaoLogin(accessToken: String) {
        val result = authDataSource.kakaoLogin(accessToken).await()
        val customToken = (result.data as Map<*, *>)["token"] as String
        authDataSource.signInWithCustomToken(customToken).await()
    }

    override suspend fun naverLogin(accessToken: String) {
        val result = authDataSource.naverLogin(accessToken).await()
        val customToken = (result.data as Map<*, *>)["token"] as String
        authDataSource.signInWithCustomToken(customToken).await()
    }

    override fun getCurrentUserId(): String? {
        return authDataSource.getCurrentUserId()
    }

    override suspend fun unlinkKakao() {
        authDataSource.unlinkKakao()
    }

    override suspend fun unlinkNaver() {
        authDataSource.unlinkNaver()
    }

    override suspend fun deleteUserAccount() {
        authDataSource.deleteUserAccount().await()
    }

    override suspend fun logout() {
        authDataSource.signOut()
    }
}
