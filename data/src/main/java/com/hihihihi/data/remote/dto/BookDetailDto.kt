package com.hihihihi.data.remote.dto

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class BookDetailDto(
    @SerializedName("item") val item: List<BookDetailItemDto>
)

@Keep
data class BookDetailItemDto(
    @SerializedName("itemId") val itemId: String?,
    @SerializedName("title") val title: String?,
    @SerializedName("author") val author: String?,
    @SerializedName("publisher") val publisher: String?,
    @SerializedName("isbn13") val isbn13: String?,
    @SerializedName("cover") val cover: String?,
    @SerializedName("subInfo") val subInfo: SubInfoDto?
)

@Keep
data class SubInfoDto(
    @SerializedName("itemPage") val itemPage: String?
) 