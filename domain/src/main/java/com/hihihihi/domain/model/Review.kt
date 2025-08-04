package com.hihihihi.domain.model

data class Review(
    val bookId: String,
    val content: String,
    val createdAt: String,
    val isPublic: String,
    val rating: String,
    val userId: String
)