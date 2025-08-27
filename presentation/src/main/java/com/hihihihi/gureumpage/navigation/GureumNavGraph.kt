package com.hihihihi.gureumpage.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.hihihihi.gureumpage.ui.bookdetail.BookDetailScreen
import com.hihihihi.gureumpage.ui.home.HomeScreen
import com.hihihihi.gureumpage.ui.library.LibraryScreen
import com.hihihihi.gureumpage.ui.login.LoginScreen
import com.hihihihi.gureumpage.ui.mindmap.MindMapScreen
import com.hihihihi.gureumpage.ui.mypage.MyPageScreen
import com.hihihihi.gureumpage.ui.onboarding.OnBoardingScreen
import com.hihihihi.gureumpage.ui.quotes.QuotesScreen
import com.hihihihi.gureumpage.ui.search.SearchScreen
import com.hihihihi.gureumpage.ui.splash.SplashView
import com.hihihihi.gureumpage.ui.statistics.StatisticsScreen
import com.hihihihi.gureumpage.ui.timer.TimerScreen
import com.hihihihi.gureumpage.ui.withdraw.WithdrawScreen

@Composable
fun GureumNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = NavigationRoute.Splash.route,
    snackbarHostState: SnackbarHostState
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(NavigationRoute.Splash.route) { SplashView(navController) }
        composable(
            route = NavigationRoute.Home.route,
            deepLinks = listOf(
                navDeepLink { uriPattern = "gureum://home" },
                navDeepLink { uriPattern = "app://home" }
            )
        ) { HomeScreen(navController = navController) }
        composable(NavigationRoute.Login.route) { LoginScreen(navController) }
        composable(NavigationRoute.OnBoarding.route) { OnBoardingScreen(navController) }
        composable(
            route = NavigationRoute.MindMap.route,
            arguments = listOf(
                navArgument("mindmapId") { type = NavType.StringType },
                navArgument("bookId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val mindmapId = backStackEntry.arguments?.getString("mindmapId")
            mindmapId?.let { MindMapScreen(mindmapId = it) }
        }
        composable(NavigationRoute.Quotes.route) { QuotesScreen() }
        composable(NavigationRoute.Library.route) { LibraryScreen(navController) }
        composable(NavigationRoute.Search.route) { SearchScreen(navController) }
        composable(
            route = NavigationRoute.StatisticsWeekly.route,
            deepLinks = listOf(
                navDeepLink { uriPattern = "gureum://statistics/weekly" },
                navDeepLink { uriPattern = "app://statistics/weekly" }
            )
        ) { StatisticsScreen(initialPreset = com.hihihihi.domain.model.DateRangePreset.WEEK) }
        composable(
            route = NavigationRoute.StatisticsMonthly.route,
            deepLinks = listOf(
                navDeepLink { uriPattern = "gureum://statistics/monthly" },
                navDeepLink { uriPattern = "app://statistics/monthly" }
            )
        ) { StatisticsScreen(initialPreset = com.hihihihi.domain.model.DateRangePreset.MONTH) }
        composable(
            route = NavigationRoute.StatisticsYearly.route,
            deepLinks = listOf(
                navDeepLink { uriPattern = "gureum://statistics/yearly" },
                navDeepLink { uriPattern = "app://statistics/yearly" }
            )
        ) { StatisticsScreen(initialPreset = com.hihihihi.domain.model.DateRangePreset.YEAR) }
        composable(
            route = NavigationRoute.Timer.route,
            arguments = listOf(navArgument("userBookId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userBookId = requireNotNull(backStackEntry.arguments?.getString("userBookId")) {
                "TimerScreen requires non-null userBookId"
            }
            TimerScreen(
                userBookId = userBookId,
                onExit = {
                    navController.popBackStack()

                    navController.navigate(NavigationRoute.BookDetail.createRoute(userBookId)) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
        composable(NavigationRoute.MyPage.route) { MyPageScreen(navController = navController) }
        composable(
            route = NavigationRoute.BookDetail.route,
            arguments = listOf(navArgument("bookId") { type = NavType.StringType }),
            deepLinks = listOf(
                navDeepLink { uriPattern = "gureum://bookdetail/{bookId}" },
                navDeepLink { uriPattern = "app://bookdetail/{bookId}" }
            )
        ) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString("bookId")
            bookId?.let {
                BookDetailScreen(
                    bookId = it,
                    navController = navController,
                    snackbarHostState = snackbarHostState
                )
            }
        }

        composable(
            route = NavigationRoute.Withdraw.route,
            arguments = listOf(navArgument("userName") { type = NavType.StringType })
        ) { backStackEntry ->
            val userName = backStackEntry.arguments?.getString("userName")
            userName?.let {
                WithdrawScreen(userName = it, navController = navController)
            }
        }
    }
}