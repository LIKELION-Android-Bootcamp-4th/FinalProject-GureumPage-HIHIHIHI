package com.hihihihi.data.remote.dto

import androidx.annotation.Keep
import com.google.firebase.firestore.PropertyName
import com.hihihihi.domain.model.Book

@Keep
data class BookDto(
    var bookId: String = "",

    @get:PropertyName("title") @set:PropertyName("title")
    var title: String = "",

    @get:PropertyName("author") @set:PropertyName("author")
    var author: String = "",

    @get:PropertyName("publisher") @set:PropertyName("publisher")
    var publisher: String = "",

    @get:PropertyName("isbn_10") @set:PropertyName("isbn_10")
    var isbn10: String = "",

    @get:PropertyName("isbn_13") @set:PropertyName("isbn_13")
    var isbn13: String = "",

    @get:PropertyName("description") @set:PropertyName("description")
    var description: String = "",

    @get:PropertyName("genre_ids") @set:PropertyName("genre_ids")
    var genreIds: List<String> = emptyList(),

    @get:PropertyName("image_url") @set:PropertyName("image_url")
    var imageUrl: String = "",

    @get:PropertyName("total_page") @set:PropertyName("total_page")
    var totalPage: Int = 0
)