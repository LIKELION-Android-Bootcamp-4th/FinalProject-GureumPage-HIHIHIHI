package com.hihihihi.data.mapper

import com.google.firebase.firestore.FieldValue
import com.hihihihi.data.common.util.toLocalDateTime
import com.hihihihi.data.common.util.toTimestamp
import com.hihihihi.data.remote.dto.QuoteDto
import com.hihihihi.domain.model.Quote
import com.hihihihi.domain.model.RecordType

fun Quote.toDto(): QuoteDto = QuoteDto(
    quoteId = id,
    userId = userId,
    userBookId = userBookId,
    content = content,
    pageNumber = pageNumber,
    isLiked = isLiked,
    createdAt = createdAt?.toTimestamp(),
    title = title,
    author = author,
    publisher = publisher,
    imageUrl = imageUrl,
)

fun QuoteDto.toDomain(): Quote = Quote(
    id = quoteId,
    userId = userId,
    userBookId = userBookId,
    content = content,
    pageNumber = pageNumber,
    isLiked = isLiked,
    createdAt = createdAt?.toLocalDateTime(),
    title = title,
    author = author,
    publisher = publisher,
    imageUrl = imageUrl,
)

fun QuoteDto.toMap(): Map<String, Any?> {
    return mapOf(
        "user_id" to userId,
        "userbook_id" to userBookId,
        "content" to content,
        "page_number" to pageNumber,
        "is_liked" to isLiked,
        "created_at" to FieldValue.serverTimestamp(),
        "title" to title,
        "author" to author,
        "publisher" to publisher,
        "image_url" to imageUrl,
    )
}
