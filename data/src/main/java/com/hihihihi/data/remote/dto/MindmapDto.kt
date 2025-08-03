package com.hihihihi.data.remote.dto

import androidx.annotation.Keep
import com.google.firebase.firestore.PropertyName

@Keep
data class MindmapDto(
    var mindmapId: String = "",

    @get:PropertyName("userbook_id") @set:PropertyName("userbook_id")
    var userBookId: String = ""
)