package com.hihihihi.data.remote.dto

import androidx.annotation.Keep
import com.google.firebase.firestore.PropertyName
import com.hihihihi.domain.model.MindmapNode
import com.hihihihi.domain.model.MindmapNodeType


@Keep
data class MindmapNodeDto(
    @get:PropertyName("mindmap_id") @set:PropertyName("mindmap_id")
    var mindmapId: String = "",

    @get:PropertyName("node_id") @set:PropertyName("node_id")
    var nodeId: String = "",

    @get:PropertyName("node_title") @set:PropertyName("node_title")
    var nodeTitle: String = "",

    @get:PropertyName("node_ex") @set:PropertyName("node_ex")
    var nodeEx: String = "",

    @get:PropertyName("node_parent") @set:PropertyName("node_parent")
    var nodeParent: String? = null,

    @get:PropertyName("type") @set:PropertyName("type")
    var type: String = ""
)