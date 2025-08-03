package com.hihihihi.domain.model

data class Book(
    val title: String,
    val author: String,
    val publisher: String,
    val isbn10: String,
    val isbn13: String,
    val description: String,
    val genreIds: List<String>,
    val imageUrl: String,
    val totalPage: Int
)