package com.hihihihi.gureumpage.navigation


sealed class NavigationRoute(val route: String) {
    object Splash : NavigationRoute("splash")
    object Home : NavigationRoute("home")
    object Login : NavigationRoute("login")
    object OnBoarding : NavigationRoute("onboarding")
    object MindMap : NavigationRoute("mindmap/{bookId}/{mindmapId}") {
        fun createRoute(bookId: String, mindmapId: String?): String = "mindmap/$bookId/$mindmapId"
    }

    object Quotes : NavigationRoute("quotes")
    object Library : NavigationRoute("library")
    object Search : NavigationRoute("search")
    object StatisticsWeekly : NavigationRoute("statistics/weekly")
    object StatisticsMonthly : NavigationRoute("statistics/monthly")
    object StatisticsYearly : NavigationRoute("statistics/yearly")
    object Timer : NavigationRoute("timer/{userBookId}") {
        fun createRoute(userBookId: String): String = "timer/$userBookId"
    }

    object MyPage : NavigationRoute("mypage")
    object BookDetail : NavigationRoute("bookdetail/{bookId}?showAddQuote={showAddQuote}&showAddManualRecord={showAddManualRecord}") {
        fun createRoute(
            bookId: String,
            showAddQuote: Boolean = false,
            showAddManualRecord: Boolean = false
        ): String {
            val route = "bookdetail/$bookId?showAddQuote=$showAddQuote&showAddManualRecord=$showAddManualRecord"
            return route
        }
    }

    object Withdraw : NavigationRoute("withdraw/{userName}") {
        fun createRoute(userName: String): String = "withdraw/$userName"
    }
}