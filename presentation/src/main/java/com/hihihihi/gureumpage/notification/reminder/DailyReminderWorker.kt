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
import com.hihihihi.domain.usecase.user.CheckRecentVisitUseCase
import com.hihihihi.gureumpage.notification.common.Channels
import com.hihihihi.gureumpage.notification.common.NotificationFactory
import com.hihihihi.gureumpage.notification.common.Quiet
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class DailyReminderWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted params: WorkerParameters,
    private val factory: NotificationFactory,
    private val checkRecentVisitUseCase: CheckRecentVisitUseCase,
) : CoroutineWorker(appContext, params) {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override suspend fun doWork(): Result {
        Channels.ensureAll(appContext)

        // 7일간 안 들어온 경우
        val thresholdMillis = inputData.getLong("thresholdMillis", 1000 * 60 * 60 * 24 * 7)
        val isRecent = checkRecentVisitUseCase(thresholdMillis)

        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(appContext, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return Result.success()
        }

        // 무음 시간이거나 오랫동안 안 들어오면 알림 스킵
        if (!Quiet.allow() || !isRecent) return Result.success()

        val notReadToday = true
        if (notReadToday) {
            val pendingIntent = factory.pendingIntentTo("gureum://read/start".toUri())
            val notification = factory.simpleAlarm(
                Channels.REMINDER,
                "구름이의 독서 알림 ☁\uFE0F",
                "10분만 읽어도 충분해요.",
                pendingIntent
            )
            factory.notify("reminder:daily", 10001, notification)
        }

        ReminderScheduler.scheduleDaily(appContext, hour = 22, minute = 0)

        return Result.success()
    }
}
