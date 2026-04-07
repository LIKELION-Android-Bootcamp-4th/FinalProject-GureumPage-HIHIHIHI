package com.hihihihi.presentation.ui.model

import com.hihihihi.domain.usecase.user.MyPageData
import java.time.LocalDate

data class MyPageUiModel(
    val nickname: String?,
    val appellation: String?,
    val provider: String?,
    val readingStats: Map<LocalDate, Int>,
    val totalBooks: Int,
    val totalPages: Int,
    val totalReadMinutes: Int,
)

fun MyPageData.toUiModel() = MyPageUiModel(
    nickname = user?.nickname,
    appellation = user?.appellation,
    provider = user?.provider,
    readingStats = readingStats,
    totalBooks = totalBooks,
    totalPages = totalPages,
    totalReadMinutes = totalReadMinutes,
)
