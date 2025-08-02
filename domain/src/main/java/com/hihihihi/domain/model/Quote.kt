package com.hihihihi.domain.model

import java.time.LocalDateTime

data class Quote(
    val id: String,
    val userId: String,
    val userBookId: String,
    val content: String,
    val pageNumber: Int?,
    val isLiked: Boolean,
    val createdAt: LocalDateTime?
)
