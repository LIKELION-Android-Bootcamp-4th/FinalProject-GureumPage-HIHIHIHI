package com.hihihihi.data.remote.dto

import androidx.annotation.Keep
import com.google.firebase.firestore.PropertyName

@Keep
data class ReviewDto(
    var reviewId: String = "",

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
)