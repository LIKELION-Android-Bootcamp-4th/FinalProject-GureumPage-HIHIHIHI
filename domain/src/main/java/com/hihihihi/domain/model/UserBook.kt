package com.hihihihi.domain.model

import java.time.LocalDateTime

data class UserBook (
    val userBookId: String = "",
    val userId: String = "",
    val isbn10: String? = null,
    val isbn13: String? = null,
    val title: String = "",
    val author: String = "",
    val imageUrl: String = "",
    val isLiked: Boolean = false,
    val totalPage: Int = 0,
    val currentPage: Int = 0,
    val startDate: LocalDateTime? = null,
    val endDate: LocalDateTime? = null,
    val totalReadTime: Int = 0,
    val status: ReadingStatus = ReadingStatus.PLANNED,
    val review: String? = null,
    val rating: Double? = null,
    val category: String? = null,
    val createdAt: LocalDateTime? = null
)

