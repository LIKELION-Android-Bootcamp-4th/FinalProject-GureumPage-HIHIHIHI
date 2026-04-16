package com.hihihihi.gureumpage.notification.reminder

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.time.Duration
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

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

    fun cancel(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork("daily-reminder")
    }
}