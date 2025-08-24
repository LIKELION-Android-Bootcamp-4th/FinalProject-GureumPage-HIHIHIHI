package com.hihihihi.gureumpage.notification.progress

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.hihihihi.gureumpage.notification.common.Quiet
import java.time.LocalDate
import androidx.core.content.edit
import androidx.core.net.toUri
import com.hihihihi.gureumpage.notification.common.Channels
import com.hihihihi.gureumpage.notification.common.NotificationFactory

object DailyGoalNotifier {
    private const val PREFERENCE = "goal_progress"
    private val day
        get() = LocalDate.now().toString()

    fun onProgress(context: Context, totalSecond: Int, goalSecond: Int) {
        if (goalSecond <= 0 || totalSecond < 0) return
        if (!Quiet.allow()) return

        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) return

        val ratio = (totalSecond.toFloat() / goalSecond).coerceAtLeast(0f)

        // 조건 달성 알림을 한 번만 보내기 위해 SharedPreferences 사용
        val sharedPref = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE)

        // 새로운 날이면 SharedPreferences 초기화
        val today = day
        val lastDay = sharedPref.getString("day", null)
        if (lastDay != day) {
            sharedPref.edit {
                putString("day", today)
                    .putFloat("last_ratio", 0f)
                    .putBoolean("sent80", false)
                    .putBoolean("sent100", false)
            }
        }

        val prev = sharedPref.getFloat("last_ratio", 0f)
        var sent80 = sharedPref.getBoolean("sent80", false)
        var sent100 = sharedPref.getBoolean("sent100", false)

        val factory = NotificationFactory(context)

        // 80%
        // 80% 알림을 보내지 않고, 이전 값이 80보다 작으며 현재 값이 80보다 크거나 같을 때
        if (!sent80 && prev < 0.8f && ratio >= 0.8f) {
            val pendingIntent = factory.pendingIntentTo("gureum://goals/today".toUri())
            val notification = factory.simpleAlarm(Channels.PROGRESS, "오늘 목표 80% 달성", "", pendingIntent)
            factory.notify("goals:today", 12080, notification)
            sent80 = true
        }

        // 100%
        if (!sent100 && prev < 1.0f && ratio >= 1.0f) {
            val pendingIntent = factory.pendingIntentTo("gureum://stats/daily".toUri())
            val notification = factory.simpleAlarm(Channels.PROGRESS, "오늘 목표 달성 🎉 통계 보기", "", pendingIntent)
            factory.notify("goals:today", 12100, notification)
            sent100 = true
        }

        sharedPref.edit {
            putFloat("last_ratio", ratio)
                .putBoolean("sent80", sent80)
                .putBoolean("sent100", sent100)
        }
    }
}