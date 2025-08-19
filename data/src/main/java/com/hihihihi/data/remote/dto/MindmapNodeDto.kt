package com.hihihihi.data.remote.dto

import androidx.annotation.Keep
import com.google.firebase.firestore.PropertyName

@Keep
data class MindmapNodeDto(
    @get:PropertyName("user_id") @set:PropertyName("user_id")
    var userId: String = "",

    var mindmapNodeId: String = "",

    var mindmapId: String = "",

    var nodeTitle: String = "",

    var nodeEx: String = "",

    var parentNodeId: String? = null,

    var color: String? = null,

    var icon: String? = null,

    var deleted: Boolean = false,

    var bookImage: String? = null,
)