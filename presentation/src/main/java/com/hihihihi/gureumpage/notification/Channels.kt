package com.hihihihi.gureumpage.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

object Channels {
    const val CHANNEL_ID = "reading_updates"

    // 채널 생성 - 앱 시작 시 한 번 호출
    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT < 26) return

        val notiManager = context.getSystemService(NotificationManager::class.java)

        if (notiManager.getNotificationChannel(CHANNEL_ID) == null) {
            notiManager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID,
                    "구름 알림",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "독서 진행, 리마인더 등"
                }
            )
        }
    }
}
