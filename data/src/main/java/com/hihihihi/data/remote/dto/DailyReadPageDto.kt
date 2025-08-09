package com.hihihihi.data.remote.dto

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

@Keep
data class DailyReadPageDto(
    var docId: String = "",

    @get:PropertyName("uid") @set:PropertyName("uid")
    var uid: String = "",

    @get:PropertyName("date") @set:PropertyName("date")
    var date: String = "",

    @get:PropertyName("totalReadPageCount") @set:PropertyName("totalReadPageCount")
    var totalReadPageCount: Long = 0L,

    @get:PropertyName("updated_at") @set:PropertyName("updated_at")
    var updatedAt: Timestamp? = null
)