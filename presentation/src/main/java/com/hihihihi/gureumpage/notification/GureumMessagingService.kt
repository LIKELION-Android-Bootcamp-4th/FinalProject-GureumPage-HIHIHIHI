package com.hihihihi.gureumpage.notification

import android.Manifest
import androidx.annotation.RequiresPermission
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.hihihihi.domain.notification.PushTokenRegistrar
import dagger.hilt.android.AndroidEntryPoint
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
        Channels.ensureChannel(this)
    }

    // 최신 FCM 토큰 서버에 업로드
    override fun onNewToken(token: String) {
        tokenRegistrar.upsert(token)
    }

    // 메시지 수신
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onMessageReceived(message: RemoteMessage) {
        val title = message.data["title"] ?: "구름한장"
        val body = message.data["body"] ?: "알림"
        val bookId = message.data["bookId"]

        val pendingIntent = if (bookId != null) factory.bookDetail(bookId)
        else factory.bookDetail("recent")

        factory.notify(message.hashCode(), factory.simpleAlarm(title, body, pendingIntent))
    }
}