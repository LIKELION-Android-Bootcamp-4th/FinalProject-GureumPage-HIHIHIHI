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
import com.hihihihi.domain.model.DailyReadPage
import com.hihihihi.domain.usecase.daily.GetDailyReadPagesByUserIdAndDateUseCase
import com.hihihihi.domain.usecase.daily.GetDailyReadPagesUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

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

            // 한달전 첫번째 날
            val dayOfStart = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)

                set(Calendar.DAY_OF_MONTH, 1)
                add(Calendar.MONTH, -1)
            }

            val dailies = getDailyReadPagesByUserIdAndDateUseCase(currentUid,dayOfStart.time).first()

            val grouped: Map<LocalDate, Int> = dailies
                .groupBy { it.date } // it.date: LocalDate
                .mapValues { (_, items) -> items.sumOf { it.totalReadPageCount } }

            val payload: HeatPayload = if (grouped.isEmpty()) {
                DummyData.buildDummyPayload()
            } else {
                buildPayloadFrom(grouped,dayOfStart.time)
            }

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
            Log.e("Widget","HistoryHeatMapWorker.doWork()",e)
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
        pages <= 0      -> 0
        pages < t2      -> 1   // [t1, t2)
        pages < t3      -> 2   // [t2, t3)
        pages < t4      -> 3   // [t3, t4)
        else            -> 4   // [t4, ∞)
    }

    private fun buildPayloadFrom(
        grouped: Map<LocalDate, Int>,
        dayOfStart: Date
    ): HeatPayload {
        // dayOfStart: "이전달 1일" (UTC로 계산해 왔으므로, 위젯 표시에 사용할 현지 TZ로 변환)
        val zoneId = TimeZone.getDefault().toZoneId()
        val prevMonthFirst = dayOfStart.toInstant().atZone(zoneId).toLocalDate()

        // 이번달 1일 (split 기준)
        val currentMonthFirst = prevMonthFirst.plusMonths(1).withDayOfMonth(1)

        // 좌측 4주(이전달 블록) 시작점을 '일요일'에 맞춤
        val start = currentMonthFirst
            .minusWeeks(4)
            .with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))

        // 9주(56일) 범위 끝 (토요일)
        val end = start.plusDays(62)

        // 월 라벨: 좌=이전달, 우=이번달
        val monthLeft = "${prevMonthFirst.monthValue}월"
        val monthRight = "${currentMonthFirst.monthValue}월"

        // 63칸 그리드 (9열 × 7행)
        val levels = ArrayList<HeatCell>(63).apply { repeat(63) { add(HeatCell()) } }

        var offset = 0
        var d = start
        while (!d.isAfter(end)) {
            val col = offset / 7                         // 0..7 (8열)
            val row = if (d.dayOfWeek == DayOfWeek.SUNDAY) 0 else d.dayOfWeek.value // 0..6
            val idx = row * 9 + col

            val pages = grouped[d] ?: 0
            levels[idx] = HeatCell(
                ymd = d.toString(),
                level = calcLevel(pages)
            )

            d = d.plusDays(1)
            offset++
        }

        return HeatPayload(
            monthLeft = monthLeft,
            monthRight = monthRight,
            levels = levels
        )
    }
}