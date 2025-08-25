package com.hihihihi.gureumpage.notification.common

import android.Manifest
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.hihihihi.gureumpage.R

class NotificationFactory(private val context: Context) {

    // URI 딥 링크를 Activity로 전달할 PendingIntent 생성
    fun pendingIntentTo(uri: Uri): PendingIntent {
        val intent = Intent(Intent.ACTION_VIEW, uri)
            .setPackage(context.packageName) // 앱 내에서만 라우팅
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE

        return PendingIntent.getActivity(context, uri.toString().hashCode(), intent, flags)
    }

    // 기본 알림
    fun simpleAlarm(channelId: String, title: String, text: String, content: PendingIntent): Notification =
        NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_cloud_reading)
            .setContentTitle(title)
            .setContentText(text)
            .setContentIntent(content)
            .setAutoCancel(true)
            .build()

    // 알람 표시
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun notify(tag: String?, id: Int, notification: Notification) {
        val notiManager = NotificationManagerCompat.from(context)

        if (tag.isNullOrEmpty()) notiManager.notify(id, notification)
        else notiManager.notify(tag, id, notification)
    }
}