package com.hihihihi.presentation.ui.model

import com.hihihihi.domain.usecase.user.HomeData

data class HomeUiModel(
    val nickname: String,
    val appellation: String,
    val dailyGoalTime: Int,
    val userBooks: List<UserBookUiModel>,
    val quotes: List<QuoteUiModel>,
    val todayReadTime: Int,
)

fun HomeData.toUiModel() = HomeUiModel(
    nickname = user.nickname,
    appellation = user.appellation,
    dailyGoalTime = user.dailyGoalTime,
    userBooks = userBooks.map { it.toUiModel() },
    quotes = quotes.map { it.toUiModel() },
    todayReadTime = todayReadTime,
)
