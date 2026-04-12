package com.hihihihi.presentation.ui.model

import androidx.compose.runtime.Immutable
import com.hihihihi.domain.model.Quote
import java.time.LocalDateTime

@Immutable
data class QuoteUiModel(
    val id: String,
    val content: String,
    val pageNumber: Int?,
    val isLiked: Boolean,
    val createdAt: LocalDateTime?,
    val title: String,
    val author: String,
    val publisher: String,
    val imageUrl: String,
)

fun Quote.toUiModel() = QuoteUiModel(
    id = id,
    content = content,
    pageNumber = pageNumber,
    isLiked = isLiked,
    createdAt = createdAt,
    title = title,
    author = author,
    publisher = publisher,
    imageUrl = imageUrl,
)
