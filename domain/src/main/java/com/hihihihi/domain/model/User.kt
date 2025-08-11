package com.hihihihi.domain.model

import java.time.LocalDateTime

//도메인 유저 모델
data class User(
    val userId: String,
    val nickname: String,
    val appellation: String,   // 칭호
    val dailyGoalTime: Int,
    val provider: String,
    val createdAt: LocalDateTime? = null
)