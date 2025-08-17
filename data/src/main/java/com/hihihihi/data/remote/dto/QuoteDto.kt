package com.hihihihi.data.remote.dto

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName

@Keep
data class QuoteDto(
    @get:Exclude @set:Exclude
    var quoteId: String = "",

    @get:PropertyName("title") @set:PropertyName("title")
    var title: String = "",

    @get:PropertyName("author") @set:PropertyName("author")
    var author: String = "",

    @get:PropertyName("publisher") @set:PropertyName("publisher")
    var publisher: String = "",

    @get:PropertyName("image_url") @set:PropertyName("image_url")
    var imageUrl: String = "",

    @get:PropertyName("user_id") @set:PropertyName("user_id")
    var userId: String = "",

    @get:PropertyName("userbook_id") @set:PropertyName("userbook_id")
    var userBookId: String = "",

    @get:PropertyName("content") @set:PropertyName("content")
    var content: String = "",

    @get:PropertyName("page_number") @set:PropertyName("page_number")
    var pageNumber: Int? = null,

    @get:PropertyName("is_liked") @set:PropertyName("is_liked")
    var isLiked: Boolean = false,

    @get:PropertyName("created_at") @set:PropertyName("created_at")
    var createdAt: Timestamp? =  null
)
