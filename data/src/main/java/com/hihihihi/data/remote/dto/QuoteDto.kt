package com.hihihihi.data.remote.dto

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import com.hihihihi.data.common.util.toLocalDateTime
import com.hihihihi.domain.model.Quote

@Keep
data class QuoteDto(
    @get:PropertyName("quote_id") @set:PropertyName("quote_id")
    var quoteId: String = "",

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
    var createdAt: Timestamp? = null
)
