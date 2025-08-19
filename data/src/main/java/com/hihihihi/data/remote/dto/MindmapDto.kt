package com.hihihihi.data.remote.dto

import androidx.annotation.Keep
import com.google.firebase.firestore.PropertyName

@Keep
data class MindmapDto(
    @get:PropertyName("user_id") @set:PropertyName("user_id")
    var userId: String = "",

    var mindmapId: String = "",

    var userBookId: String = "",

    var rootNodeId: String? = null,
)