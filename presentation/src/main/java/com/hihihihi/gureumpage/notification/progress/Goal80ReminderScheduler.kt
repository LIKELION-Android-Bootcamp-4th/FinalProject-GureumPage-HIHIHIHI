package com.hihihihi.gureumpage.notification.progress

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.time.Duration
import java.time.LocalDate
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

object Goal80ReminderScheduler {
    private fun workNameForToday(): String = "goal-80-reminder-" + LocalDate.now()

    fun scheduleAt(context: Context, hour: Int = 20, minute: Int = 0) {
        val now = ZonedDateTime.now()
        var next = now.withHour(hour).withMinute(minute).withSecond(0).withNano(0)
        if (!next.isAfter(now)) next = next.plusDays(1)
        val delayMin = Duration.between(now, next).toMinutes()

        val request = OneTimeWorkRequestBuilder<Goal80ReminderWorker>()
            .setInitialDelay(delayMin, TimeUnit.MINUTES)
            .addTag("goal-80-reminder")
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            workNameForToday(), ExistingWorkPolicy.REPLACE, request
        )
    }

    fun cancelToday(context: Context) {
        WorkManager.getInstance(context)
            .cancelUniqueWork(workNameForToday())
    }
}
