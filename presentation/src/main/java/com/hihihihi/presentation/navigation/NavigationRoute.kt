package com.hihihihi.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable data object Splash
@Serializable data object Home
@Serializable data object Login
@Serializable data object OnBoarding
@Serializable data object Quotes
@Serializable data object Library
@Serializable data object Search
@Serializable data object MyPage
@Serializable data object StatisticsWeekly
@Serializable data object StatisticsMonthly
@Serializable data object StatisticsYearly
@Serializable data class MindMap(val bookId: String, val mindmapId: String?)
@Serializable data class Timer(val userBookId: String)
@Serializable data class BookDetail(
    val bookId: String,
    val showAddQuote: Boolean = false,
    val showAddManualRecord: Boolean = false
)
@Serializable data class Withdraw(val userName: String)
