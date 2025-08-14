package com.hihihihi.domain.model

import java.time.LocalDateTime

data class Statistics(
    val category: List<CategorySlice>,
    val time: List<TimeSlice>,
    val pages: List<Page>,
    val xLabels: List<String>
)

data class CategorySlice(
    val label: String,
    val value: Float
)

data class TimeSlice(
    val label: String,
    val value: Float
)

data class Page(
    val label: String,
    val x: Float,
    val y: Float
)

enum class DateRangePreset {
    WEEK, MONTH, THREE_MONTH, SIX_MONTH, YEAR
}

data class DateRange(
    val start: LocalDateTime,
    val end: LocalDateTime,
)