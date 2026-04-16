package com.hihihihi.presentation.ui.model

import androidx.compose.runtime.Immutable
import com.hihihihi.domain.model.ReadingStatus
import com.hihihihi.domain.model.UserBook
import java.time.LocalDateTime

@Immutable
data class UserBookUiModel(
    val userBookId: String,
    val title: String,
    val author: String,
    val imageUrl: String,
    val status: ReadingStatus,
    val currentPage: Int,
    val totalPage: Int,
    val startDate: LocalDateTime?,
    val endDate: LocalDateTime?,
    val totalReadTime: Int,
    val rating: Double?,
    val review: String?,
    val category: String?,
    val publisher: String?,
    val isbn13: String?,
    val description: String?,
)

fun UserBookUiModel.isRead() = status == ReadingStatus.FINISHED

fun UserBook.toUiModel() = UserBookUiModel(
    userBookId = userBookId,
    title = title,
    author = author,
    imageUrl = imageUrl,
    status = status,
    currentPage = currentPage,
    totalPage = totalPage,
    startDate = startDate,
    endDate = endDate,
    totalReadTime = totalReadTime,
    rating = rating,
    review = review,
    category = category,
    publisher = publisher,
    isbn13 = isbn13,
    description = description,
)
