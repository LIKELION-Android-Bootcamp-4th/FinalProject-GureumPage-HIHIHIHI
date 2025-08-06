package com.hihihihi.data.remote.datasource

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.functions.HttpsCallableResult

interface AuthDataSource {
    fun signInWithGoogleCredential(idToken: String): Task<AuthResult>
    fun signInWithCustomToken(token: String): Task<AuthResult>
    fun requestCustomToken(functionName: String, accessToken: String): Task<HttpsCallableResult>
}
