package com.hihihihi.domain.model

import java.time.LocalDateTime

data class UserBook (
    val userBookId: String,
    val userId: String,
    val bookId: String,
    val title: String,
    val author: String,
    val imageUrl: String,
    val currentPage: Int = 0,
    val startDate: LocalDateTime? = null,
    val endDate: LocalDateTime? = null,
    val totalReadTime: Int = 0,
    val status: ReadingStatus,
    val review: String? = null,
    val rating: Double? = null,
    val createdAt: LocalDateTime? = null
)

