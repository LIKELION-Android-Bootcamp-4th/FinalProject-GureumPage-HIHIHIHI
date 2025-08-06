package com.hihihihi.data.remote.datasourceimpl

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.HttpsCallableResult
import com.hihihihi.data.remote.datasource.AuthDataSource
import javax.inject.Inject

class AuthDataSourceImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val functions: FirebaseFunctions
) : AuthDataSource {

    override fun signInWithGoogleCredential(idToken: String): Task<AuthResult> {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        return auth.signInWithCredential(credential)
    }

    override fun signInWithCustomToken(token: String): Task<AuthResult> {
        return auth.signInWithCustomToken(token)
    }

    override fun requestCustomToken(functionName: String, accessToken: String): Task<HttpsCallableResult> {
        return functions
            .getHttpsCallable(functionName)
            .call(mapOf("accessToken" to accessToken))
    }
}
