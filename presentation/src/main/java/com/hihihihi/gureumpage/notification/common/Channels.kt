package com.hihihihi.gureumpage.notification.common

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

object Channels {
    const val REMINDER = "reminder"
    const val PROGRESS = "progress"
    const val SUMMARY = "summary"
    const val ACTIVITY = "activity"

    // 채널 생성 - 앱 시작 시 한 번 호출
    fun ensureAll(context: Context) {
        if (Build.VERSION.SDK_INT < 26) return

        val notiManager = context.getSystemService(NotificationManager::class.java)

        fun createChannel(id: String, name: String, importance: Int, description: String) {
            if (notiManager.getNotificationChannel(id) == null) {
                notiManager.createNotificationChannel(
                    NotificationChannel(id, name, importance).apply {
                        this.description = description
                    }
                )
            }
        }

        createChannel(REMINDER, "리마인더", NotificationManager.IMPORTANCE_DEFAULT, "읽기 시작 알림")
        createChannel(PROGRESS, "진척/목표", NotificationManager.IMPORTANCE_DEFAULT, "목표·연속 기록")
        createChannel(SUMMARY, "요약/리포트", NotificationManager.IMPORTANCE_LOW, "주간·월간 요약")
        createChannel(ACTIVITY, "활동 유도", NotificationManager.IMPORTANCE_DEFAULT, "기록 유도")
    }

    // 채널 ID 매핑 - data["ch"]
    fun idOf(key: String?) = when (key) {
        REMINDER -> REMINDER
        PROGRESS -> PROGRESS
        SUMMARY -> SUMMARY
        else -> ACTIVITY
    }
}
