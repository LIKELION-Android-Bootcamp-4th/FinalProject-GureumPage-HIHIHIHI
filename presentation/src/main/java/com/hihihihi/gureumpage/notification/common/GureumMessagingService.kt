package com.hihihihi.gureumpage.notification.common

import android.Manifest
import androidx.annotation.RequiresPermission
import androidx.core.net.toUri
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.hihihihi.domain.notification.PushTokenRegistrar
import com.hihihihi.gureumpage.notification.schema.PushPayload
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalTime
import javax.inject.Inject

// 서비스의 엔트리 포인트
@AndroidEntryPoint
class GureumMessagingService : FirebaseMessagingService() {
    @Inject
    lateinit var factory: NotificationFactory

    @Inject
    lateinit var tokenRegistrar: PushTokenRegistrar

    // 서비스 시작 시 채널 활성화
    override fun onCreate() {
        super.onCreate()
        Channels.ensureAll(this)
    }

    // 최신 FCM 토큰 서버에 업로드
    override fun onNewToken(token: String) {
        tokenRegistrar.upsert(token)
    }

    // 메시지 수신
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onMessageReceived(message: RemoteMessage) {
//        if (!Quiet.allow()) return

        PushPayload.from(message.data)?.let { payload ->
            val notification = factory.simpleAlarm(
                Channels.idOf(payload.channelId),
                payload.title,
                payload.body,
                factory.pendingIntentTo(payload.uri)
            )
            factory.notify(payload.collapseKey, message.hashCode(), notification)
            return
        }

        message.notification?.let { noti ->
            val title = noti.title ?: return
            val body = noti.body ?: ""
            val n = factory.simpleAlarm(Channels.ACTIVITY, title, body, factory.pendingIntentTo("gureum://home".toUri()))
            factory.notify(null, message.hashCode(), n)
        }
    }
}

// 야간 알림 시간 여부
object Quiet {
    fun allow(start: Int = 22, end: Int = 7, now: Int = LocalTime.now().hour): Boolean =
        if (start <= end) now !in start until end else !(now >= start || now < end)
}
