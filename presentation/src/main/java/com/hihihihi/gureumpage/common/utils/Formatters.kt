package com.hihihihi.gureumpage.common.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
