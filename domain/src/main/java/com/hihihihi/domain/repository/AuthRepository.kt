package com.hihihihi.domain.repository

import android.content.Intent

interface AuthRepository {
    suspend fun handleGoogleSignInResult(data: Intent?)

    suspend fun signInWithCustomToken(functionName: String, accessToken: String)

    suspend fun kakaoLogin(accessToken: String)
    suspend fun naverLogin(accessToken: String)
}