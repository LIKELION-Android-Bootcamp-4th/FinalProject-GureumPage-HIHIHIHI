package com.hihihihi.data.remote.mapper

import com.hihihihi.data.remote.dto.UserBookDto
import com.hihihihi.data.common.util.toLocalDateTime
import com.hihihihi.data.common.util.toTimestamp
import com.hihihihi.domain.model.ReadingStatus
import com.hihihihi.domain.model.UserBook

fun UserBookDto.toDomain(): UserBook {
    return UserBook(
        userBookId = userBookId,
        userId = userId,
        isbn10 = isbn10,
        isbn13 = isbn13,
        title = title,
        author = author,
        isLiked = isLiked,
        imageUrl = imageUrl,
        totalPage = totalPage,
        currentPage = currentPage ?: 0,
        startDate = startDate?.toLocalDateTime(),
        endDate = endDate?.toLocalDateTime(),
        totalReadTime = totalReadTime ?: 0,
        status = ReadingStatus.valueOf(status.uppercase()),
        review = review,
        rating = rating,
        category = category,
        createdAt = createdAt?.toLocalDateTime()
    )
}

fun UserBook.toDto(includeUserBookId: Boolean = true): UserBookDto {
    return UserBookDto(
        userBookId = if (includeUserBookId) this.userBookId else "",
        userId = this.userId,
        isbn10 = this.isbn10,
        isbn13 = this.isbn13,
        title = this.title,
        author = this.author,
        isLiked = this.isLiked,
        imageUrl = this.imageUrl,
        totalPage = this.totalPage,
        currentPage = this.currentPage,
        startDate = this.startDate?.toTimestamp(),
        endDate = this.endDate?.toTimestamp(),
        totalReadTime = this.totalReadTime,
        status = this.status.name.lowercase(),
        review = this.review,
        rating = this.rating,
        category = this.category,
        createdAt = this.createdAt?.toTimestamp()
    )
}


fun UserBookDto.toMap(): Map<String, Any?> {
    return mapOf(
        "user_id" to userId,
        "isbn_10" to isbn10,
        "isbn_13" to isbn13,
        "title" to title,
        "author" to author,
        "is_liked" to isLiked,
        "image_url" to imageUrl,
        "total_page" to totalPage,
        "current_page" to currentPage,
        "start_date" to startDate,
        "end_date" to endDate,
        "total_read_time" to totalReadTime,
        "status" to status,
        "review" to review,
        "rating" to rating,
        "category" to category,
        "created_at" to createdAt
    )
}
