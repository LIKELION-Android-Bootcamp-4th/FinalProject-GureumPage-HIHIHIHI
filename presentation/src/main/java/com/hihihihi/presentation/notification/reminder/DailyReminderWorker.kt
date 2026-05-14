package com.hihihihi.presentation.notification.reminder

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
import com.hihihihi.domain.usecase.notification.GetNotificationSettingsUseCase
import com.hihihihi.domain.usecase.user.CheckRecentVisitUseCase
import com.hihihihi.presentation.notification.common.Channels
import com.hihihihi.presentation.notification.common.NotificationFactory
import com.hihihihi.presentation.notification.common.Quiet
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

@HiltWorker
class DailyReminderWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted params: WorkerParameters,
    private val factory: NotificationFactory,
    private val checkRecentVisitUseCase: CheckRecentVisitUseCase,
    private val getNotificationSettingsUseCase: GetNotificationSettingsUseCase,
) : CoroutineWorker(appContext, params) {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override suspend fun doWork(): Result {
        Channels.ensureAll(appContext)
        val settings = getNotificationSettingsUseCase().first()

        if (!settings.isDailyReminderEnabled) {
            ReminderScheduler.cancel(appContext)
            return Result.success()
        }

        // 7일간 안 들어온 경우
        val thresholdMillis = inputData.getLong("thresholdMillis", TimeUnit.DAYS.toMillis(7))
        val isRecent = checkRecentVisitUseCase(thresholdMillis).getOrDefault(false)

        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(appContext, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ReminderScheduler.scheduleDaily(appContext, settings.reminderHour, settings.reminderMinute)
            return Result.success()
        }

        // 무음 시간이거나 오랫동안 안 들어오면 알림 스킵
        if (!Quiet.allow() || !isRecent) {
            ReminderScheduler.scheduleDaily(appContext, settings.reminderHour, settings.reminderMinute)
            return Result.success()
        }

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

        ReminderScheduler.scheduleDaily(appContext, settings.reminderHour, settings.reminderMinute)

        return Result.success()
    }
}
