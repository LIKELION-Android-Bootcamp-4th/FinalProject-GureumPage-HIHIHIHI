package com.hihihihi.gureumpage

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.hihihihi.presentation.widgets.common.WigetAutoUpdater
import com.kakao.sdk.common.KakaoSdk
import com.navercorp.nid.NidOAuth
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class GureumApp : Application(), Configuration.Provider {
    @Inject lateinit var workerFactory: HiltWorkerFactory
    @Inject lateinit var widgetAutoUpdater: WigetAutoUpdater
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    override val workManagerConfiguration: Configuration
        get() {
            return Configuration.Builder()
                .setWorkerFactory(workerFactory)
                .setMinimumLoggingLevel(Log.VERBOSE)
                .build()
        }

    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)

        NidOAuth.initialize(
            this,
            BuildConfig.NAVER_CLIENT_ID,
            BuildConfig.NAVER_CLIENT_SECRET,
            "구름한장",
        )

        // 위젯 자동 업데이터
        widgetAutoUpdater.start()
    }

    override fun onTerminate() {
        super.onTerminate()
        coroutineScope.launch {
            widgetAutoUpdater.stop()
        }
    }
}
