package com.hihihihi.data.remote.dto

import androidx.annotation.Keep
import com.google.firebase.firestore.PropertyName
import com.hihihihi.domain.model.Genre

@Keep
data class GenreDto(
    var genreId: String = "",

    @get:PropertyName("genre_name") @set:PropertyName("genre_name")
    var genreName: String = ""
)