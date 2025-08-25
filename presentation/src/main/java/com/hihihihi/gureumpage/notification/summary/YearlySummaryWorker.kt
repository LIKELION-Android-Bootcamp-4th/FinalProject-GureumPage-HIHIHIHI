package com.hihihihi.gureumpage.notification.summary

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.hihihihi.gureumpage.notification.common.NotificationFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class YearlySummaryWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val factory: NotificationFactory,
    private val summaryProvider: SummaryProvider
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(appContext, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return Result.success()
        }

        val (title, body) = summaryProvider.yearly()
        val pendingIntent = factory.pendingIntentTo("gureum://statistics/yearly".toUri())
        factory.notify("summary:yearly", 31003, factory.summary(title, body, pendingIntent))

        SummaryScheduler.scheduleYearly(appContext)

        return Result.success()
    }
}