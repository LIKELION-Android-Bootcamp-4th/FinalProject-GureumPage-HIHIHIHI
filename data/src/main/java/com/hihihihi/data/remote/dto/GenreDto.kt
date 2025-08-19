package com.hihihihi.data.remote.dto

import androidx.annotation.Keep
import com.google.firebase.firestore.PropertyName

@Keep
data class GenreDto(
    var genreId: String = "",

    @get:PropertyName("genre_name") @set:PropertyName("genre_name")
    var genreName: String = ""
)