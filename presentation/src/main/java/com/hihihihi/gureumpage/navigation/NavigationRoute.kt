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
    object Timer: NavigationRoute("timer/{bookId}"){
        fun createRoute(bookId: String): String = "timer/$bookId"
    }
    object MyPage: NavigationRoute("mypage")
    object BookDetail : NavigationRoute("bookdetail/{bookId}") {
        fun createRoute(bookId: String): String = "bookdetail/$bookId"
    }}