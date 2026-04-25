package com.hihihihi.data.remote.datasourceimpl

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.HttpsCallableResult
import com.hihihihi.data.remote.datasource.AuthDataSource
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

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

    override fun kakaoLogin(accessToken: String): Task<HttpsCallableResult> {
        return functions.getHttpsCallable("kakaoCustomAuth").call(mapOf("accessToken" to accessToken))
    }

    override fun naverLogin(accessToken: String): Task<HttpsCallableResult> {
        return functions.getHttpsCallable("naverCustomAuth").call(mapOf("accessToken" to accessToken))
    }

    override fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    override suspend fun unlinkKakao() {
        return kotlin.coroutines.suspendCoroutine { continuation ->
            com.kakao.sdk.user.UserApiClient.instance.unlink { error ->
                if (error != null) {
                    continuation.resumeWithException(error)
                } else {
                    continuation.resume(Unit)
                }
            }
        }
    }

    override suspend fun unlinkNaver() {
        return kotlin.coroutines.suspendCoroutine { continuation ->
            com.navercorp.nid.oauth.NidOAuthLogin()
                .callDeleteTokenApi(object : com.navercorp.nid.oauth.OAuthLoginCallback {
                    override fun onSuccess() {
                        continuation.resume(Unit)
                    }

                    override fun onFailure(httpStatus: Int, message: String) {
                        continuation.resumeWithException(Exception(message))
                    }

                    override fun onError(errorCode: Int, message: String) {
                        continuation.resumeWithException(Exception(message))
                    }
                })
        }
    }

    override fun deleteUserAccount(): Task<HttpsCallableResult> {
        return functions.getHttpsCallable("deleteUserAccount").call()
    }

    override fun signOut() {
        FirestoreListenerManager.clearAll()
        auth.signOut()
    }
}
