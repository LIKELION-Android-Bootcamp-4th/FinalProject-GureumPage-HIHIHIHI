package com.hihihihi.domain.model

import java.time.LocalDateTime

data class History(
    val id: String,
    val userId: String,
    val userBookId: String,
    val date: LocalDateTime?,
    val startTime: LocalDateTime?,
    val endTime: LocalDateTime?,
    val readTime: Int,
    val readPageCount: Int,
    val recordType: RecordType
)
