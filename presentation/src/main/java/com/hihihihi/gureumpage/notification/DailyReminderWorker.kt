package com.hihihihi.gureumpage.notification

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.hihihihi.gureumpage.notification.common.Channels
import com.hihihihi.gureumpage.notification.common.NotificationFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.Duration
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

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

object ReminderScheduler {
    fun scheduleDaily(context: Context, hour: Int = 20, minute: Int = 0) {
        val now = ZonedDateTime.now()
        var next = now.withHour(hour).withMinute(minute).withSecond(0).withNano(0)
        if (!next.isAfter(now)) next = next.plusDays(1)
        val delayMin = Duration.between(now, next).toMinutes()

        val request = OneTimeWorkRequestBuilder<DailyReminderWorker>()
            .setInitialDelay(delayMin, TimeUnit.MINUTES)
            .addTag("daily-reminder-once")
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "daily-reminder", ExistingWorkPolicy.REPLACE, request
        )
    }
}
