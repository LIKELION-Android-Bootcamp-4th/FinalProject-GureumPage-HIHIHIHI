package com.hihihihi.data.remote.dto

import androidx.annotation.Keep
import com.google.firebase.firestore.PropertyName
import com.hihihihi.domain.model.Review

@Keep
data class ReviewDto(
    @get:PropertyName("book_id") @set:PropertyName("book_id")
    var bookId: String = "",

    @get:PropertyName("content") @set:PropertyName("content")
    var content: String = "",

    @get:PropertyName("created_at") @set:PropertyName("created_at")
    var createdAt: String = "",

    @get:PropertyName("is_public") @set:PropertyName("is_public")
    var isPublic: String = "",

    @get:PropertyName("rating") @set:PropertyName("rating")
    var rating: String = "",

    @get:PropertyName("user_id") @set:PropertyName("user_id")
    var userId: String = ""
) {
    fun toDomain() = Review(
        bookId = bookId,
        content = content,
        createdAt = createdAt,
        isPublic = isPublic,
        rating = rating,
        userId = userId
    )
}