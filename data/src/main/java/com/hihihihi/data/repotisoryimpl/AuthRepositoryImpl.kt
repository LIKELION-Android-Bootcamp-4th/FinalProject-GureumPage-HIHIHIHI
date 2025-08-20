package com.hihihihi.data.repotisoryimpl

import android.content.Intent
import kotlinx.coroutines.tasks.await
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.hihihihi.data.remote.datasource.AuthDataSource
import com.hihihihi.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: AuthDataSource,
) : AuthRepository {

    override suspend fun handleGoogleSignInResult(data: Intent?) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        val account = task.result
        val idToken = account.idToken ?: throw Exception("idToken is null")
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
}