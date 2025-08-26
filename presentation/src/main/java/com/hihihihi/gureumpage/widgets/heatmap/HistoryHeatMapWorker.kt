package com.hihihihi.gureumpage.widgets.heatmap

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.hihihihi.domain.usecase.daily.GetDailyReadPagesByUserIdAndDateUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

@HiltWorker
class HistoryHeatMapWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val getDailyReadPagesByUserIdAndDateUseCase: GetDailyReadPagesByUserIdAndDateUseCase
) : CoroutineWorker(appContext, params) {

    companion object {
        const val UNIQUE_WORK_NAME = "HeatMapWorker"
    }

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val currentUid: String
        get() = auth.currentUser!!.uid


    override suspend fun doWork(): Result {
        try {
            val manager = GlanceAppWidgetManager(applicationContext)
            val glanceIds = manager.getGlanceIds(HistoryHeatMapWidget::class.java)

            // 6주 전부터 다음주까지의 데이터가 필요하므로 충분히 이전 날짜부터 조회
            val today = LocalDate.now()
            val dataStartDate =
                today.minusWeeks(8).atStartOfDay().atZone(java.time.ZoneId.systemDefault())
            val startDate = java.util.Date.from(dataStartDate.toInstant())

            val dailies = getDailyReadPagesByUserIdAndDateUseCase(currentUid, startDate).first()

            val grouped: Map<LocalDate, Int> = dailies
                .groupBy { it.date } // it.date: LocalDate
                .mapValues { (_, items) -> items.sumOf { it.totalReadPageCount } }

            val payload: HeatPayload = buildPayloadFrom(grouped)

            val jsonData = Gson().toJson(payload)

            glanceIds.forEach { id ->
                updateAppWidgetState(applicationContext, id) { prefs ->
                    val dataKey = stringPreferencesKey(HistoryHeatMapWidget.WIDGET_DATA_KEY)
                    prefs[dataKey] = jsonData
                }
                HistoryHeatMapWidget().update(applicationContext, id)
            }
            return Result.success()
        } catch (e: Exception) {
            Log.e("Widget", "HistoryHeatMapWorker.doWork()", e)
        }
        return Result.failure()

    }

    /**
     * 읽은 페이지 수를 0~4 레벨로 변환.
     * 기본 구간:
     * 0 -> 0
     * 1..9 -> 1
     * 10..19 -> 2
     * 20..29 -> 3
     * 30+ -> 4
     */
    fun calcLevel(
        pages: Int,
        t1: Int = 1,   // level 1 하한
        t2: Int = 10,  // level 2 하한
        t3: Int = 20,  // level 3 하한
        t4: Int = 30   // level 4 하한
    ): Int = when {
        pages <= 0 -> 0
        pages < t2 -> 1   // [t1, t2)
        pages < t3 -> 2   // [t2, t3)
        pages < t4 -> 3   // [t3, t4)
        else -> 4   // [t4, ∞)
    }

    private fun buildPayloadFrom(
        grouped: Map<LocalDate, Int>,
    ): HeatPayload {
        val today = LocalDate.now()

        // 이번주를 7번째 열에 위치시키기 위해 6주 전부터 시작
        val thisWeekSunday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
        val start = thisWeekSunday.minusWeeks(6) // 6주 전부터 시작 (6주 전 + 이번주 + 다음주 = 8주)

        // 56칸 그리드 (8열 × 7행)
        val levels = ArrayList<HeatCell>(56).apply { repeat(56) { add(HeatCell()) } }
        val monthHeaders = ArrayList<String>(8).apply { repeat(8) { add("") } }

        // 각 열(주)에 대해 처리
        for (col in 0 until 7) {
            val weekStart = start.plusWeeks(col.toLong())
            var hasFirstDay = false
            var monthOfFirstDay: String? = null

            // 해당 주의 7일 처리
            for (row in 0 until 7) {
                val date = weekStart.plusDays(row.toLong())
                val idx = col * 7 + row

                // 1일인 날짜 찾기
                if (date.dayOfMonth == 1) {
                    hasFirstDay = true
                    monthOfFirstDay = "${date.monthValue}월"
                }

                val pages = grouped[date] ?: 0
                levels[idx] = HeatCell(
                    ymd = date.toString(),
                    level = calcLevel(pages)
                )
            }

            // 해당 열에 1일이 있으면 월 헤더 설정
            if (hasFirstDay && monthOfFirstDay != null) {
                monthHeaders[col] = monthOfFirstDay
            }
        }

        return HeatPayload(
            monthHeaders = monthHeaders,
            levels = levels
        )
    }
}