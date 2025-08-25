package com.hihihihi.gureumpage.widgets.heatmap

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.TemporalAdjusters

object DummyData {
    // 데이터 없을 때: 63칸 꽉 채우는 더미 패턴(일관성 있는 결정적 값)
    public fun buildDummyPayload(): HeatPayload {
        val today = LocalDate.now()
        val end = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY))
        val start = end.minusDays(41)

        val months = monthsInRange(start, end)
        val monthLeft = months.getOrNull(0) ?: ""
        val monthRight = months.getOrNull(1) ?: ""

        val levels = ArrayList<HeatCell>(42)

        for (col in 0 until 6) {
            for (row in 0 until 7) {
                val date = start.plusDays((col * 7 + row).toLong())
                // 간단한 더미 규칙: 날짜/요일/주차 기반 0~4 반복
                val level = ((date.dayOfMonth + col + row) % 5)
                levels.add(HeatCell(ymd = date.toString(), level = level))
            }
        }

        return HeatPayload(
            monthLeft = monthLeft,
            monthRight = monthRight,
            levels = levels
        )
    }

    // 일요일=0, 월=1 ... 토=6
    private fun sundayRowIndex(dow: DayOfWeek): Int =
        if (dow == DayOfWeek.SUNDAY) 0 else dow.value

    // 범위에 포함된 월 텍스트(좌/우)
    private fun monthsInRange(start: LocalDate, end: LocalDate): List<String> {
        val set = linkedSetOf<YearMonth>()
        var cur = YearMonth.from(start)
        val last = YearMonth.from(end)
        while (!cur.isAfter(last)) {
            set.add(cur)
            cur = cur.plusMonths(1)
        }
        return when (set.size) {
            0 -> emptyList()
            1 -> listOf(set.first().monthValue.toString() + "월", "")
            else -> listOf(
                set.first().monthValue.toString() + "월",
                set.last().monthValue.toString() + "월"
            )
        }
    }

}