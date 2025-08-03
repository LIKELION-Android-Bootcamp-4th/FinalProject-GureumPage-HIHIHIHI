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
        bookId = bookId,
        title = title,
        author = author,
        imageUrl = imageUrl,
        totalPage = totalPage,
        currentPage = currentPage ?: 0,
        startDate = startDate?.toLocalDateTime(),
        endDate = endDate?.toLocalDateTime(),
        totalReadTime = totalReadTime ?: 0,
        status = ReadingStatus.valueOf(status.uppercase()),
        review = review,
        rating = rating,
        createdAt = createdAt?.toLocalDateTime()
    )
}

fun UserBook.toDto(): UserBookDto {
    return UserBookDto(
        userBookId = this.userBookId,
        userId = this.userId,
        bookId = this.bookId,
        title = this.title,
        author = this.author,
        imageUrl = this.imageUrl,
        totalPage = this.totalPage,
        currentPage = this.currentPage,
        startDate = this.startDate?.toTimestamp(),
        endDate = this.endDate?.toTimestamp(),
        totalReadTime = this.totalReadTime,
        status = this.status.name.lowercase(),
        review = this.review,
        rating = this.rating,
        createdAt = this.createdAt?.toTimestamp()
    )
}