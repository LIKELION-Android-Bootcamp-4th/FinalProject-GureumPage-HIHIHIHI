package com.hihihihi.gureumpage.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.hihihihi.gureumpage.ui.bookdetail.BookDetailScreen
import com.hihihihi.gureumpage.ui.home.HomeScreen
import com.hihihihi.gureumpage.ui.library.LibraryScreen
import com.hihihihi.gureumpage.ui.login.LoginScreen
import com.hihihihi.gureumpage.ui.mindmap.MindMapScreen
import com.hihihihi.gureumpage.ui.mypage.MyPageScreen
import com.hihihihi.gureumpage.ui.onboarding.OnBoardingScreen
import com.hihihihi.gureumpage.ui.quotes.QuotesScreen
import com.hihihihi.gureumpage.ui.search.SearchScreen
import com.hihihihi.gureumpage.ui.statistics.StatisticsScreen
import com.hihihihi.gureumpage.ui.timer.TimerScreen


@Composable
fun GureumNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = NavigationRoute.Home.route,
        modifier = modifier
    ){
        composable(NavigationRoute.Home.route) { HomeScreen() }
        composable(NavigationRoute.Login.route) { LoginScreen() }
        composable(NavigationRoute.OnBoarding.route) { OnBoardingScreen() }
        composable(NavigationRoute.MindMap.route) { MindMapScreen() }
        composable(NavigationRoute.Quotes.route) { QuotesScreen() }
        composable(NavigationRoute.Library.route) { LibraryScreen() }
        composable(NavigationRoute.Search.route) { SearchScreen() }
        composable(NavigationRoute.Statistics.route) { StatisticsScreen() }
        composable(NavigationRoute.Timer.route) { TimerScreen() }
        composable(NavigationRoute.MyPage.route) { MyPageScreen() }
        composable(NavigationRoute.BookDetail.route) { BookDetailScreen() }
    }
}