package com.hihihihi.presentation.ui.login.util

import android.app.Activity
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
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

    suspend fun getGoogleIdToken(activity: Activity, webClientId: String): String {
        val credentialManager = CredentialManager.create(activity)
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(webClientId)
            .build()
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
        val result = credentialManager.getCredential(activity, request)

        // 타입 체크: CustomCredential 중 Google ID Token이 아닌 경우 명시적 예외
        val credential = result.credential
        if (credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            return GoogleIdTokenCredential.createFrom(credential.data).idToken
        }
        throw IllegalStateException("Unexpected credential type: ${credential.type}")
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
                    } else if (token != null && cont.isActive) {
                        cont.resume(token.accessToken)
                    } else {
                        // token/error 모두 null → 웹 로그인으로 fallback
                        UserApiClient.instance.loginWithKakaoAccount(activity, callback = callback)
                    }
                }
            } else {
                UserApiClient.instance.loginWithKakaoAccount(activity, callback = callback)
            }
        }

}
