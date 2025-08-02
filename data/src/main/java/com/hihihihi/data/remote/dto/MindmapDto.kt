package com.hihihihi.data.remote.dto

import androidx.annotation.Keep
import com.google.firebase.firestore.PropertyName
import com.hihihihi.domain.model.Mindmap

@Keep
data class MindmapDto(
    @get:PropertyName("mindmap_id") @set:PropertyName("mindmap_id")
    var mindmapId: String = "",

    @get:PropertyName("userbook_id") @set:PropertyName("userbook_id")
    var userbookId: String = ""
) {
    fun toDomain() = Mindmap(
        mindmapId = mindmapId,
        userbookId = userbookId
    )
}

