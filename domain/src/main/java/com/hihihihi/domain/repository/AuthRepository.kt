package com.hihihihi.domain.repository

interface AuthRepository {

    suspend fun googleLogin(idToken: String)

    suspend fun signInWithCustomToken(functionName: String, accessToken: String)

    suspend fun kakaoLogin(accessToken: String)

    suspend fun naverLogin(accessToken: String)

    fun getCurrentUserId(): String?

    suspend fun unlinkKakao()

    suspend fun unlinkNaver()

    suspend fun deleteUserAccount()

    suspend fun logout()
}
