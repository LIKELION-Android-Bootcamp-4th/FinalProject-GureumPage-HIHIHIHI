package com.hihihihi.gureumpage.common.utils

import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.NumberFormat

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