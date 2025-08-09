package com.hihihihi.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

data class DailyReadPage(
    val date: LocalDate,
    val totalReadPageCount: Int,
    val uid: String,
    val updatedAt: LocalDateTime?
)