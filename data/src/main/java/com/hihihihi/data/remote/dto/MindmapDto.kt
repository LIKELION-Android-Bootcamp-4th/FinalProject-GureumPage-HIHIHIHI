package com.hihihihi.data.remote.dto

import androidx.annotation.Keep

@Keep
data class MindmapDto(
    var mindmapId: String = "",

    var userBookId: String = "",

    var rootNodeId: String? = null,
)