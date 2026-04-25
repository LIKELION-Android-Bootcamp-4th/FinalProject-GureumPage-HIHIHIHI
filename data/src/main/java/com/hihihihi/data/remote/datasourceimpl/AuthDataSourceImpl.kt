package com.hihihihi.data.remote.datasourceimpl

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.HttpsCallableResult
import com.hihihihi.data.remote.datasource.AuthDataSource
import com.navercorp.nid.NidOAuth
import com.navercorp.nid.oauth.util.NidOAuthCallback
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class AuthDataSourceImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val functions: FirebaseFunctions,
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
        return suspendCancellableCoroutine { continuation ->
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
        return suspendCancellableCoroutine { cont ->
            try {
                NidOAuth.disconnect(
                    object : NidOAuthCallback {
                        override fun onSuccess() {
                            if (cont.isActive) cont.resume(Unit)
                        }

                        override fun onFailure(errorCode: String, errorDesc: String) {
                            if (cont.isActive) {
                                cont.resumeWithException(Exception("Naver unlink failed: $errorCode, $errorDesc"))
                            }
                        }
                    },
                )
            } catch (e: Exception) {
                if (cont.isActive) cont.resumeWithException(e)
            }
            cont.invokeOnCancellation { /* Naver SDK는 별도 취소 API 없음 */ }
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
