package com.hihihihi.data.mapper

import com.hihihihi.data.common.util.toLocalDateTime
import com.hihihihi.data.common.util.toTimestamp
import com.hihihihi.data.remote.dto.UserDto
import com.hihihihi.domain.model.User

// Firestore -> Domain
fun UserDto.toDomain(): User = User(
    userId = userId,
    nickname = nickname.orEmpty(),
    appellation = appellation.orEmpty(),
    dailyGoalTime = dailyGoalTime ?: 0,
    provider = provider.orEmpty(),
    createdAt = createdAt?.toLocalDateTime()
)

// Domain -> Firestore (저장/업데이트용)
fun User.toDto(): UserDto = UserDto(
    appellation = appellation,
    createdAt = createdAt?.toTimestamp(),
    dailyGoalTime = dailyGoalTime,
    nickname = nickname,
    provider = provider
)