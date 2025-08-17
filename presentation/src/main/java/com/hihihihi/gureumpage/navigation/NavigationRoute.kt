package com.hihihihi.gureumpage.navigation

sealed class NavigationRoute (val route: String) {
    object Splash: NavigationRoute("splash")
    object Home: NavigationRoute("home")
    object Login: NavigationRoute("login")
    object OnBoarding: NavigationRoute("onboarding")
    object MindMap: NavigationRoute("mindmap/{bookId}"){
        fun createRoute(bookId: String): String = "mindmap/$bookId"
    }
    object Quotes: NavigationRoute("quotes")
    object Library: NavigationRoute("library")
    object Search: NavigationRoute("search")
    object Statistics: NavigationRoute("statistics")
    object Timer: NavigationRoute("timer/{userBookId}"){
        fun createRoute(userBookId: String): String = "timer/$userBookId"
    }
    object MyPage: NavigationRoute("mypage")
    object BookDetail : NavigationRoute("bookdetail/{bookId}") {
        fun createRoute(bookId: String): String = "bookdetail/$bookId"
    }}