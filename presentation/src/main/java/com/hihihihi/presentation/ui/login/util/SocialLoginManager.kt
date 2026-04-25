package com.hihihihi.presentation.ui.login.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NidOAuth
import com.navercorp.nid.oauth.util.NidOAuthCallback
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object SocialLoginManager {

    fun getGoogleIdToken(intent: Intent): String {
        val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
        val account = task.result
        return account.idToken ?: throw Exception("Google idToken is null")
    }

    suspend fun loginWithKakao(context: Context): String =
        suspendCancellableCoroutine { cont ->
            val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                when {
                    error != null -> cont.resumeWithException(error)
                    token != null -> cont.resume(token.accessToken)
                    else -> cont.resumeWithException(Exception("Unknown kakao failure"))
                }
            }
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
                UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                    if (error != null) {
                        if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                            cont.cancel(); return@loginWithKakaoTalk
                        }
                        UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
                    } else if (token != null) {
                        cont.resume(token.accessToken)
                    }
                }
            } else {
                UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
            }
        }

    suspend fun loginWithNaver(activity: Activity): String =
        suspendCancellableCoroutine { cont ->
            NidOAuth.requestLogin(
                activity,
                object : NidOAuthCallback {
                    override fun onSuccess() {
                        val token = NidOAuth.getAccessToken()
                        if (token != null) {
                            cont.resume(token)
                        } else {
                            cont.resumeWithException(Exception("Naver accessToken is null"))
                        }
                    }

                    override fun onFailure(errorCode: String, errorDesc: String) {
                        if (errorCode == "user_cancel") {
                            cont.cancel()
                        } else {
                            cont.resumeWithException(Exception("Naver login failed: $errorCode, $errorDesc"))
                        }
                    }
                },
            )
        }
}
