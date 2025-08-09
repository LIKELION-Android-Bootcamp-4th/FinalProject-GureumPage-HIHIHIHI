package com.hihihihi.data.remote.dto

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class SearchBookListDto(
    @SerializedName("item") val books: List<SearchBookDto>
)

@Keep
data class SearchBookDto(
    @SerializedName("title") val title: String?,
    @SerializedName("author") val author: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("isbn13") val isbn: String?,
    @SerializedName("cover") val cover: String?,
    @SerializedName("categoryName") val categoryName: String?,
    @SerializedName("publisher") val publisher: String? = null
) 
