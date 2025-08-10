package com.hihihihi.domain.model

data class SearchBook(
    val title: String,
    val author: String,
    val publisher: String,
    val isbn: String,
    val description: String,
    val coverImageUrl: String,
    val categoryName: String,
)
