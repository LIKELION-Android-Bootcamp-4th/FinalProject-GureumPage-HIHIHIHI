package com.hihihihi.gureumpage.notification.progress

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.hihihihi.gureumpage.notification.common.Quiet
import java.time.LocalDate
import androidx.core.content.edit

object DailyGoalNotifier {
    private const val PREFERENCE = "goal_progress"
    private val day get() = LocalDate.now().toString()

    fun onProgress(context: Context, totalSecond: Int, goalSecond: Int) {
        if (goalSecond <= 0 || totalSecond < 0) return
        if (!Quiet.allow()) return

        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) return

        // 조건 달성 알림을 한 번만 보내기 위해 SharedPreferences 사용
        val sharedPref = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE)

        // 새로운 날이면 SharedPreferences 및 스케줄 초기화
        val today = day
        val lastDay = sharedPref.getString("day", null)
        if (lastDay != day) {
            sharedPref.edit {
                putString("day", today)
                    .putFloat("last_ratio", 0f)
                    .putBoolean("sent80", false)
            }
            Goal80ReminderScheduler.cancelToday(context)
        }

        val prev = sharedPref.getFloat("last_ratio", 0f)
        var sent80 = sharedPref.getBoolean("sent80", false)

        val ratio = (totalSecond.toFloat() / goalSecond).coerceAtLeast(0f)

        // 80% - 알림을 보내지 않고, 이전 값이 80보다 작으며 현재 값이 80보다 크거나 같을 때
        if (!sent80 && prev < 0.8f && ratio >= 0.8f) {
            Goal80ReminderScheduler.scheduleAt(context)
            sent80 = true
        }

        sharedPref.edit {
            putFloat("last_ratio", ratio).putBoolean("sent80", sent80)
        }
    }
}