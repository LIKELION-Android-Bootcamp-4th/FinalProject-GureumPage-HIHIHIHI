package com.hihihihi.data.remote.dto

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import com.hihihihi.data.common.util.toLocalDateTime
import com.hihihihi.domain.model.ReadingStatus
import com.hihihihi.domain.model.UserBook

@Keep
data class UserBookDto(

    @get:PropertyName("user_book_id") @set:PropertyName("user_book_id")
    var userBookId: String = "",

    @get:PropertyName("user_id") @set:PropertyName("user_id")
    var userId: String = "",

    @get:PropertyName("book_id") @set:PropertyName("book_id")
    var bookId: String = "",

    @get:PropertyName("title") @set:PropertyName("title")
    var title: String = "",

    @get:PropertyName("author") @set:PropertyName("author")
    var author: String = "",

    @get:PropertyName("image_url") @set:PropertyName("image_url")
    var imageUrl: String = "",

    @get:PropertyName("current_page") @set:PropertyName("current_page")
    var currentPage: Int? = 0,

    @get:PropertyName("start_date") @set:PropertyName("start_date")
    var startDate: Timestamp? = null,

    @get:PropertyName("end_date") @set:PropertyName("end_date")
    var endDate: Timestamp? = null,

    @get:PropertyName("total_read_time") @set:PropertyName("total_read_time")
    var totalReadTime: Int? = 0,

    @get:PropertyName("status") @set:PropertyName("status")
    var status: String = "planned", // "planned", "reading", "finished"

    @get:PropertyName("review") @set:PropertyName("review")
    var review: String? = null,

    @get:PropertyName("rating") @set:PropertyName("rating")
    var rating: Double? = null,

    @get:PropertyName("created_at") @set:PropertyName("created_at")
    var createdAt: Timestamp? = null
) {
    fun toDomainUserBook(): UserBook {
        return UserBook(
            userBookId = userBookId,
            userId = userId,
            bookId = bookId,
            title = title,
            author = author,
            imageUrl = imageUrl,
            currentPage = currentPage?:0,
            startDate = startDate?.toLocalDateTime(),
            endDate = endDate?.toLocalDateTime(),
            totalReadTime = totalReadTime?:0,
            status = ReadingStatus.valueOf(status.uppercase()),
            review = review,
            rating = rating,
            createdAt = createdAt?.toLocalDateTime()
        )
    }
}
