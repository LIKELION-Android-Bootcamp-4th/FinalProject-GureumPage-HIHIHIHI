package com.hihihihi.data.mapper

import com.hihihihi.data.remote.dto.ReviewDto
import com.hihihihi.domain.model.Review

fun ReviewDto.toDomain(): Review {
    return Review(
        bookId = bookId,
        content = content,
        createdAt = createdAt,
        isPublic = isPublic,
        rating = rating,
        userId = userId
    )
}

fun Review.toDto(): ReviewDto {
    return ReviewDto(
        bookId = bookId,
        content = content,
        createdAt = createdAt,
        isPublic = isPublic,
        rating = rating,
        userId = userId
    )
}
