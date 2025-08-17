package com.hihihihi.data.remote.datasourceimpl

import android.app.Activity
import android.content.Context
import com.hihihihi.data.remote.datasource.NaverDataSource
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resumeWithException

class NaverDataSourceImpl @Inject constructor(
): NaverDataSource {

    override suspend fun signIn(activity: Activity): String = suspendCancellableCoroutine { cont ->
        NaverIdLoginSDK.authenticate(activity, object : OAuthLoginCallback {
            override fun onSuccess() {
                val naverAccessToken = NaverIdLoginSDK.getAccessToken()
                if (naverAccessToken != null) {
                    cont.resume(naverAccessToken, null)
                } else {
                    cont.resumeWithException(Exception("AccessToken is null"))
                }
            }

            override fun onFailure(httpStatus: Int, message: String) {
                cont.resumeWithException(Exception("Login failed: $message"))
            }

            override fun onError(errorCode: Int, message: String) {
                cont.resumeWithException(Exception("Error $errorCode: $message"))
            }
        })
    }
}
