package com.hihihihi.gureumpage.common.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.hihihihi.domain.model.History
import com.hihihihi.domain.model.ReadingStatus
import java.text.NumberFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

/**
 * 일반 숫자 포맷
 */
class SimplePercentFormatter(
    private val fractionDigits: Int = 0
) : ValueFormatter() {
    // getPercentInstance()가 value*100 과 %를 자동 처리
    private val formatter = NumberFormat.getPercentInstance().apply {
        minimumFractionDigits = fractionDigits
        maximumFractionDigits = fractionDigits
    }

    override fun getFormattedValue(value: Float): String = formatter.format(value / 100)
}

/**
 * 전체 백분율 포맷
 */
class PercentageOfTotalFormatter(
    private val total: Float,
    private val fractionDigits: Int = 1
) : ValueFormatter() {
    private val formatter = NumberFormat.getPercentInstance().apply {
        minimumFractionDigits = fractionDigits
        maximumFractionDigits = fractionDigits
    }

    override fun getBarLabel(entry: BarEntry): String {
        val ratio = if (total == 0f) 0f else entry.y / total
        return formatter.format(ratio)
    }
}

/**
 * 실수 -> 정수 포맷
 */
class PageFormatter : ValueFormatter() {
    private val formatter = NumberFormat.getIntegerInstance()

    override fun getFormattedValue(value: Float): String = formatter.format(value.toDouble())
}

/**
 * LocalDateTime -> "yyyy.MM.dd" 포맷
 */
fun formatDateToSimpleString(dateTime: LocalDateTime?): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
    return dateTime?.format(formatter) ?: ""
}

/**
 * Long 타입 밀리세컨드 -> LocalDateTime
 */
fun formatMillisToLocalDateTime(millis: Long): LocalDateTime {
    return Instant.ofEpochMilli(millis)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .atStartOfDay()
}

/**
 * 초(int)로 받은거 -> "n시간 n분 n초" 포맷
 */
fun formatSecondsToReadableTime(seconds: Int): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60

    return buildString {
        if (hours > 0) append("${hours}시간 ")
        if (minutes > 0) append("${minutes}분 ")
        if (secs > 0 || (hours == 0 && minutes == 0)) append("${secs}초")
    }.trim()
}

/**
 * 초(int)로 받은 거 -> "n시간 n분" 포맷 (초는 제외)
 */
fun formatSecondsToReadableTimeWithoutSecond(seconds: Int): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60

    return buildString {
        if (hours > 0) append("${hours}시간 ")
        if (minutes > 0) append("${minutes}분")
        if (hours == 0 && minutes == 0) append("0분")
    }.trim()
}

/**
 * px 을 Dp 로 변환
 */
@Composable
fun pxToDp(px: Int): Dp {
    val density = LocalDensity.current
    return with(density) { px.toDp() }
}

/**
 * LocalDateTime 을 HH:mm 형식으로 변환, 범위 있음
 */
fun formatTimeRange(startTime: LocalDateTime?, endTime: LocalDateTime?): String {
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    val start = startTime?.format(timeFormatter) ?: "--:--"
    val end = endTime?.format(timeFormatter) ?: "--:--"
    return "$start ~ $end"
}

/**
 * LocalDateTime 과 오늘 혹은 입력받은 endDate 날짜 비교해서 일수 계산
 */
fun getDayCountLabel(
    startDateTime: LocalDateTime,
    endDateTime: LocalDateTime?,
    status: ReadingStatus
): String {
    val startDate = startDateTime.toLocalDate()
    val endDate = if (status == ReadingStatus.READING) LocalDate.now() else endDateTime!!.toLocalDate()

    val days = ChronoUnit.DAYS.between(startDate, endDate) + 1
    val label = if (days > 999) "999+" else days.toString()

    return if (status == ReadingStatus.FINISHED) {
        // 완독이면 "123" 형식
        "${label}일"
    } else {
        // 그 외는 "123일"
        "${label}일째"
    }
}

/**
 * History에서 하루 별 독서 시간 평균 계산
 */
fun getDailyAverageReadTimeInSeconds(histories: List<History>): String {
    val dateAveragesInSeconds = histories
        .filter { it.date != null }
        .groupBy { it.date!!.toLocalDate() }
        .map { (_, dailyList) ->
            dailyList.map { it.readTime }.average()
        }

    return if (dateAveragesInSeconds.isNotEmpty()) {
        formatSecondsToReadableTime(dateAveragesInSeconds.average().toInt())
    } else {
        "0분"
    }
}

/**
 * DisplayTimeToSeconds HH:mm:ss 같은 형식을 초로 바꿔줌
 */
fun parseDisplayTimeToSeconds(display: String): Int {
    // 예: "00:45", "12:05", "01:10:30" 형태 모두 대응
    val parts = display.split(":").mapNotNull { it.toIntOrNull() }
    return when (parts.size) {
        3 -> parts[0] * 3600 + parts[1] * 60 + parts[2] // HH:mm:ss
        2 -> parts[0] * 60 + parts[1]                   // mm:ss
        else -> 0
    }
}