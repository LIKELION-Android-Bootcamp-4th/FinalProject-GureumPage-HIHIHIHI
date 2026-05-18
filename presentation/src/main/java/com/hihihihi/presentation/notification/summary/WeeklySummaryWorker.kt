package com.hihihihi.presentation.notification.summary

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.annotation.SuppressLint
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.hihihihi.domain.usecase.notification.GetNotificationSettingsUseCase
import com.hihihihi.presentation.notification.common.NotificationFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class WeeklySummaryWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val factory: NotificationFactory,
    private val summaryProvider: SummaryProvider,
    private val getNotificationSettingsUseCase: GetNotificationSettingsUseCase,
) : CoroutineWorker(appContext, workerParams) {

    @SuppressLint("MissingPermission")
    override suspend fun doWork(): Result {
        val settings = getNotificationSettingsUseCase().first()
        if (!settings.isWeeklySummaryEnabled) {
            SummaryScheduler.cancelWeekly(appContext)
            return Result.success()
        }

        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(appContext, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return Result.success()
        }

        // 요약 생성
        val (title, body) = summaryProvider.weekly()
        val pendingIntent = factory.pendingIntentTo("gureum://statistics/weekly".toUri())
        factory.notify("summary:weekly", 31001, factory.summary(title, body, pendingIntent))

        // 다음 월요일 09:00 재예약
        SummaryScheduler.scheduleWeekly(appContext)

        return Result.success()
    }
}
