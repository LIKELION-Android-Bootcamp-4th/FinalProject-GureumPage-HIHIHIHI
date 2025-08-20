package com.hihihihi.gureumpage.ui.statistics

data class Genre(
    val value: Float,
    val label: String,
)

data class ReadingTime(
    val x: Float,
    val y: Float,
)

data class PageCount(
    val x: Float,
    val y: Float,
)

val mockGenreList = listOf(
    Genre(3f, "소설"),
    Genre(4f, "판타지"),
    Genre(5f, "자기계발"),
)

val mockReading = listOf(
    ReadingTime(0f, 30f),
    ReadingTime(1f, 20f),
    ReadingTime(2f, 50f),
    ReadingTime(3f, 10f),
    ReadingTime(4f, 40f),
)

val mockPages = listOf(
    PageCount(0f, 30f),
    PageCount(1f, 40f),
    PageCount(2f, 10f),
    PageCount(3f, 60f),
    PageCount(4f, 20f),
    PageCount(5f, 10f),
    PageCount(6f, 70f),
)
