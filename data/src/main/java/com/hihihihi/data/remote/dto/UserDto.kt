package com.hihihihi.data.remote.dto

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

@Keep
data class UserDto(

    var userId: String = "",

    @get:PropertyName("appellation") @set:PropertyName("appellation")
    var appellation: String? = "",

    @get:PropertyName("createdAt") @set:PropertyName("createdAt")
    var createdAt: Timestamp? = null,

    @get:PropertyName("daily_goal_time") @set:PropertyName("daily_goal_time")
    var dailyGoalTime: Int? = 0,

    @get:PropertyName("nickname") @set:PropertyName("nickname")
    var nickname: String? = null,

    @get:PropertyName("provider") @set:PropertyName("provider")
    var provider: String? = null,
)