package com.hihihihi.gureumpage.notification.reminder

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.hihihihi.gureumpage.notification.common.Channels
import com.hihihihi.gureumpage.notification.common.NotificationFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class DailyReminderWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted params: WorkerParameters,
    private val factory: NotificationFactory
) : CoroutineWorker(appContext, params) {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override suspend fun doWork(): Result {
        Channels.ensureAll(appContext)

        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(appContext, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return Result.success()
        }

        val notReadToday = true
        if (notReadToday) {
            val pendingIntent = factory.pendingIntentTo("gureum://read/start".toUri())
            // TODO 알림 메시지 설정하기
            val notification = factory.simpleAlarm(Channels.REMINDER, "오늘 읽기를 시작할까요?", "", pendingIntent)
            factory.notify("reminder:daily", 10001, notification)
        }
        return Result.success()
    }
}
