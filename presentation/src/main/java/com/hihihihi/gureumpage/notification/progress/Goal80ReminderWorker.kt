package com.hihihihi.gureumpage.notification.progress

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.hihihihi.gureumpage.notification.common.Channels
import com.hihihihi.gureumpage.notification.common.NotificationFactory
import com.hihihihi.gureumpage.notification.common.Quiet
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.LocalDate

@HiltWorker
class Goal80ReminderWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted params: WorkerParameters,
    private val factory: NotificationFactory
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(appContext, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return Result.success()
        }

        if (!Quiet.allow()) return Result.success()

        val preference = appContext.getSharedPreferences("goal_progress", Context.MODE_PRIVATE)
        val today = LocalDate.now().toString()

        val lastDay = preference.getString("day", null)
        val lastRatio = preference.getFloat("last_ratio", 0f)

        if (today == lastDay && lastRatio >= 0.8f) {
            val pendingIntent = factory.pendingIntentTo("gureum://goals/today".toUri())
            val notification = factory.simpleAlarm(Channels.PROGRESS, "오늘 목표 80% 달성", "", pendingIntent)
            factory.notify("goals:today", 12000, notification)
        }

        return Result.success()
    }
}