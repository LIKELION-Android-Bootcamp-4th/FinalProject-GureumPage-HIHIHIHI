package com.hihihihi.data.remote.datasourceimpl

import android.content.Context
import com.hihihihi.data.remote.datasource.KakaoDataSource
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resumeWithException

class KakaoDataSourceImpl @Inject constructor(
    private val context: Context
) : KakaoDataSource {

    override suspend fun signIn(): String = suspendCancellableCoroutine { cont ->

        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                cont.resumeWithException(error)
            } else if (token != null) {
                cont.resume(token.accessToken, null)
            } else {
                cont.resumeWithException(Exception("Unknown login failure"))
            }
        }

        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                if (error != null) {
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        // 로그인 취소면 그냥 취소 처리
                        cont.cancel()
                        return@loginWithKakaoTalk
                    }
                    // 카카오톡 로그인 실패하면 카카오계정 로그인 시도
                    UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
                } else if (token != null) {
                    cont.resume(token.accessToken, null)
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
        }
    }
}
