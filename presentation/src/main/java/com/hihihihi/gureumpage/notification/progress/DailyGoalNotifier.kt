package com.hihihihi.gureumpage.notification.progress

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.hihihihi.gureumpage.notification.common.Quiet
import java.time.LocalDate
import androidx.core.content.edit
import com.hihihihi.gureumpage.notification.reminder.ReminderScheduler

object DailyGoalNotifier {
    private const val PREFERENCE = "goal_progress"
    private val day get() = LocalDate.now().toString()

    fun onProgress(context: Context, totalSecond: Int, goalSecond: Int) {
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
                putFloat("last_ratio", 0f)
                putBoolean("sent80", false)
                putBoolean("goal_enabled", false)
            }
            Goal80ReminderScheduler.cancelToday(context)

            ReminderScheduler.cancel(context)
        }

        // 목표 없으면 취소
        if (goalSecond <= 0) {
            sharedPref.edit {
                putBoolean("goal_enabled", false)
                putFloat("last_ratio", 0f)
                putBoolean("sent80", false)
            }
            Goal80ReminderScheduler.cancelToday(context)

            ReminderScheduler.scheduleDaily(context)

            return
        }

        // 목표가 있으면 알림
        sharedPref.edit { putBoolean("goal_enabled", true) }

        var sent80 = sharedPref.getBoolean("sent80", false)
        val ratio = (totalSecond.toFloat() / goalSecond).coerceAtLeast(0f)

        when {
            // 오늘 책 안 읽었으면 데일리만 예약
            totalSecond <= 0 -> {
                ReminderScheduler.scheduleDaily(context)
                Goal80ReminderScheduler.cancelToday(context)
                sharedPref.edit {
                    putFloat("last_ratio", 0f)
                    putBoolean("sent80", false)
                }
                return
            }

            // 목표르 100% 달성했으면 모든 알림 취소
            ratio >= 1f -> {
                Goal80ReminderScheduler.cancelToday(context)
                ReminderScheduler.cancel(context)
                sharedPref.edit {
                    putFloat("last_ratio", ratio)
                    putBoolean("sent80", true) // 재예약 방지 표식
                }
                return
            }

            // 목표 80% 이상 달성했으면 80% 알림,
            ratio >= 0.8f -> {
                if (!sent80) {
                    Goal80ReminderScheduler.scheduleAt(context) // 예: 20:00
                    sent80 = true
                }
                ReminderScheduler.cancel(context)
            }

            // 80% 미만 → 데일리만 예약
            else -> {
                ReminderScheduler.scheduleDaily(context)
                Goal80ReminderScheduler.cancelToday(context)
                sent80 = false
            }
        }

        sharedPref.edit {
            putFloat("last_ratio", ratio)
            putBoolean("sent80", sent80)
        }
    }
}