package com.hihihihi.data.remote.dto

import androidx.annotation.Keep

@Keep
data class MindmapNodeDto(
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