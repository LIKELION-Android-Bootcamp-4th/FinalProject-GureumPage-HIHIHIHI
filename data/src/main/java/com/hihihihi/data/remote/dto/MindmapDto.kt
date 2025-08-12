package com.hihihihi.data.remote.dto

import androidx.annotation.Keep
import com.google.firebase.firestore.PropertyName

@Keep
data class MindmapDto(
    var mindmapId: String = "",

    @get:PropertyName("userbook_id") @set:PropertyName("userbook_id")
    var userBookId: String = "",

    @get:PropertyName("root_node_id") @set:PropertyName("root_node_id")
    var rootNodeId: String? = null,
)