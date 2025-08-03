package com.hihihihi.data.remote.dto

import androidx.annotation.Keep
import com.google.firebase.firestore.PropertyName

@Keep
data class MindmapDto(
    @get:PropertyName("mindmap_id") @set:PropertyName("mindmap_id")
    var mindmapId: String = "",

    @get:PropertyName("userbook_id") @set:PropertyName("userbook_id")
    var userBookId: String = ""
)