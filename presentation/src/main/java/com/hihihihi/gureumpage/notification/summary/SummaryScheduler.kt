package com.hihihihi.gureumpage.notification.summary

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.time.Duration
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

object SummaryScheduler {
    private fun delayUntil(target: ZonedDateTime): Long =
        Duration.between(ZonedDateTime.now(), target).toMinutes().coerceAtLeast(1)

    private fun nextWeeklyAt(hour: Int, minute: Int): ZonedDateTime {
        // 알림 울릴 시간 설정
        var time = ZonedDateTime.now().withHour(hour).withMinute(minute).withSecond(0).withNano(0)
            .with(java.time.DayOfWeek.MONDAY)

        // 알림 시간이 지금 이후라면 1주 뒤로 설정
        if (!time.isAfter(ZonedDateTime.now())) time = time.plusWeeks(1)

        return time
    }

    private fun nextMonthlyAt(hour: Int, minute: Int): ZonedDateTime {
        var time = ZonedDateTime.now().withDayOfMonth(1).withHour(hour).withMinute(minute)
            .withSecond(0).withNano(0)
        if (!time.isAfter(ZonedDateTime.now())) time = time.plusMonths(1)

        return time
    }

    private fun nextYearlyAt(hour: Int, minute: Int): ZonedDateTime {
        var time = ZonedDateTime.now().withMonth(1).withDayOfMonth(1).withHour(hour).withMinute(minute)
            .withSecond(0).withNano(0)
        if (!time.isAfter(ZonedDateTime.now())) time = time.plusYears(1)

        return time
    }

    fun scheduleWeekly(context: Context, hour: Int = 9, minute: Int = 0) {
        val duration = delayUntil(nextWeeklyAt(hour, minute))
        val request = OneTimeWorkRequestBuilder<WeeklySummaryWorker>()
            .setInitialDelay(duration, TimeUnit.MINUTES)
            .addTag("summary-weekly")
            .build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            "summary-weekly", ExistingWorkPolicy.REPLACE, request
        )
    }

    fun scheduleMonthly(context: Context, hour: Int = 9, minute: Int = 0) {
        val duration = delayUntil(nextMonthlyAt(hour, minute))
        val request = OneTimeWorkRequestBuilder<MonthlySummaryWorker>()
            .setInitialDelay(duration, TimeUnit.MINUTES)
            .addTag("summary-monthly")
            .build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            "summary-monthly", ExistingWorkPolicy.REPLACE, request
        )
    }

    fun scheduleYearly(context: Context, hour: Int = 9, minute: Int = 0) {
        val duration = delayUntil(nextYearlyAt(hour, minute))
        val request = OneTimeWorkRequestBuilder<YearlySummaryWorker>()
            .setInitialDelay(duration, TimeUnit.MINUTES)
            .addTag("summary-yearly")
            .build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            "summary-yearly", ExistingWorkPolicy.REPLACE, request
        )
    }

    fun cancelAll(ctx: Context) {
        WorkManager.getInstance(ctx).cancelUniqueWork("summary-weekly")
        WorkManager.getInstance(ctx).cancelUniqueWork("summary-monthly")
        WorkManager.getInstance(ctx).cancelUniqueWork("summary-yearly")
    }

    // 테스트 알림
    fun scheduleWeeklyIn(ctx: Context, seconds: Long = 10) {
        val req = OneTimeWorkRequestBuilder<WeeklySummaryWorker>()
            .setInitialDelay(seconds, TimeUnit.SECONDS)
            .addTag("summary-weekly")
            .build()
        WorkManager.getInstance(ctx).enqueueUniqueWork(
            "summary-weekly", ExistingWorkPolicy.REPLACE, req
        )
    }

    fun scheduleMonthlyIn(ctx: Context, seconds: Long = 10) {
        val req = OneTimeWorkRequestBuilder<MonthlySummaryWorker>()
            .setInitialDelay(seconds, TimeUnit.SECONDS)
            .addTag("summary-monthly")
            .build()
        WorkManager.getInstance(ctx).enqueueUniqueWork(
            "summary-monthly", ExistingWorkPolicy.REPLACE, req
        )
    }

    fun scheduleYearlyIn(ctx: Context, seconds: Long = 10) {
        val req = OneTimeWorkRequestBuilder<YearlySummaryWorker>()
            .setInitialDelay(seconds, TimeUnit.SECONDS)
            .addTag("summary-yearly")
            .build()
        WorkManager.getInstance(ctx).enqueueUniqueWork(
            "summary-yearly", ExistingWorkPolicy.REPLACE, req
        )
    }

    fun scheduleAllIn(ctx: Context, seconds: Long = 10) {
        scheduleWeeklyIn(ctx, seconds)
        scheduleMonthlyIn(ctx, seconds)
        scheduleYearlyIn(ctx, seconds)
    }
}