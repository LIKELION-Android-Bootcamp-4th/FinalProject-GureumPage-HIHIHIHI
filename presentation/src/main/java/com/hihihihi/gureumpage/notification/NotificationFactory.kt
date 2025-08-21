package com.hihihihi.gureumpage.notification

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
import com.hihihihi.gureumpage.navigation.DeepLink

class NotificationFactory(private val context: Context) {

    // URI 딥 링크를 Activity로 전달할 PendingIntent 생성
    private fun pendingIntentTo(uri: Uri): PendingIntent {
        val intent = Intent(Intent.ACTION_VIEW, uri)
            .setPackage(context.packageName) // 앱 내에서만 라우팅
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE

        return PendingIntent.getActivity(context, uri.hashCode(), intent, flags)
    }

    // 상세 라우팅
    fun home() = pendingIntentTo(DeepLink.home())
    fun bookDetail(bookId: String) = pendingIntentTo(DeepLink.bookDetail(bookId))

    // 기본 알림
    fun simpleAlarm(title: String, text: String, content: PendingIntent): Notification =
        NotificationCompat.Builder(context, Channels.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_cloud_reading)
            .setContentTitle(title)
            .setContentText(text)
            .setContentIntent(content)
            .setAutoCancel(true)
            .build()

    // 알람 표시
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun notify(id: Int, notification: Notification) {
        NotificationManagerCompat.from(context).notify(id, notification)
    }
}