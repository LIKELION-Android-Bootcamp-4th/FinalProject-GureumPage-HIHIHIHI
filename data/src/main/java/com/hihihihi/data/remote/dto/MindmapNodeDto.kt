package com.hihihihi.data.remote.dto

import androidx.annotation.Keep
import com.google.firebase.firestore.PropertyName
import com.hihihihi.domain.model.MindmapNode
import com.hihihihi.domain.model.MindmapNodeType


@Keep
data class MindmapNodeDto(
    var mindmapNodeId: String = "",

    @get:PropertyName("mindmap_id") @set:PropertyName("mindmap_id")
    var mindmapId: String = "",

    @get:PropertyName("node_title") @set:PropertyName("node_title")
    var nodeTitle: String = "",

    @get:PropertyName("node_ex") @set:PropertyName("node_ex")
    var nodeEx: String = "",

    @get:PropertyName("parent_node_id") @set:PropertyName("parent_node_id")
    var parentNodeId: String? = null,

    @get:PropertyName("color") @set:PropertyName("color")
    var color: String? = null,

    @get:PropertyName("icon") @set:PropertyName("icon")
    var icon: String? = null,

    @get:PropertyName("deleted") @set:PropertyName("deleted")
    var deleted: Boolean = false,

)