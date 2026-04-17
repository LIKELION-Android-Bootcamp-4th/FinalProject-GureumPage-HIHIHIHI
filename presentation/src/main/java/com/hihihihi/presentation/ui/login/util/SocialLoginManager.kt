package com.hihihihi.presentation.ui.login.util

import android.app.Activity
import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object SocialLoginManager {

    suspend fun getGoogleIdToken(context: Context, webClientId: String): String {
        val credentialManager = CredentialManager.create(context)
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(webClientId)
            .build()
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
        val result = credentialManager.getCredential(context, request)
        return GoogleIdTokenCredential.createFrom(result.credential.data).idToken
    }

    suspend fun loginWithKakao(activity: Activity): String =
        suspendCancellableCoroutine { cont ->
            val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                when {
                    error != null -> cont.resumeWithException(error)
                    token != null -> cont.resume(token.accessToken)
                    else -> cont.resumeWithException(Exception("Unknown kakao failure"))
                }
            }
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(activity)) {
                UserApiClient.instance.loginWithKakaoTalk(activity) { token, error ->
                    if (error != null) {
                        if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                            cont.cancel(); return@loginWithKakaoTalk
                        }
                        UserApiClient.instance.loginWithKakaoAccount(activity, callback = callback)
                    } else if (token != null) {
                        cont.resume(token.accessToken)
                    }
                }
            } else {
                UserApiClient.instance.loginWithKakaoAccount(activity, callback = callback)
            }
        }

}
