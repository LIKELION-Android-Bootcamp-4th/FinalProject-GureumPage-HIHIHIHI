package com.hihihihi.presentation.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import com.hihihihi.domain.model.DateRangePreset
import com.hihihihi.presentation.ui.bookdetail.BookDetailScreen
import com.hihihihi.presentation.ui.home.HomeScreen
import com.hihihihi.presentation.ui.library.LibraryScreen
import com.hihihihi.presentation.ui.login.LoginScreen
import com.hihihihi.presentation.ui.mindmap.MindMapScreen
import com.hihihihi.presentation.ui.mypage.MyPageScreen
import com.hihihihi.presentation.ui.onboarding.OnBoardingScreen
import com.hihihihi.presentation.ui.quotes.QuotesScreen
import com.hihihihi.presentation.ui.search.SearchScreen
import com.hihihihi.presentation.ui.splash.SplashView
import com.hihihihi.presentation.ui.statistics.StatisticsScreen
import com.hihihihi.presentation.ui.timer.TimerScreen
import com.hihihihi.presentation.ui.withdraw.WithdrawScreen

@Composable
fun GureumNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
    pendingWidgetRoute: Any? = null,
) {
    NavHost(
        navController = navController,
        startDestination = Splash,
        modifier = modifier,
    ) {
        composable<Splash> {
            SplashView(
                onNavigateToLogin = {
                    navController.navigate(Login) {
                        popUpTo<Splash> { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateToOnBoarding = {
                    navController.navigate(OnBoarding) {
                        popUpTo<Splash> { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Home) {
                        popUpTo<Splash> { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateToWidget = { route: Any ->
                    navController.navigate(route) {
                        popUpTo<Splash> { inclusive = true }
                        launchSingleTop = true
                    }
                },
                pendingWidgetRoute = pendingWidgetRoute,
            )
        }

        composable<Home>(
            deepLinks = listOf(
                navDeepLink { uriPattern = "gureum://home" },
                navDeepLink { uriPattern = "app://home" },
            ),
        ) {
            HomeScreen(
                onNavigateToBookDetail = { bookId ->
                    navController.navigate(BookDetail(bookId = bookId))
                },
                onNavigateToSearch = {
                    navController.navigate(Search)
                },
            )
        }

        composable<Login> {
            LoginScreen(
                onNavigateToHome = {
                    navController.navigate(Home) {
                        popUpTo<Login> { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateToOnBoarding = {
                    navController.navigate(OnBoarding) {
                        popUpTo<Login> { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
        }

        composable<OnBoarding> {
            OnBoardingScreen(
                onNavigateToHome = {
                    navController.navigate(Home) {
                        popUpTo<OnBoarding> { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateBack = { navController.popBackStack() },
            )
        }

        composable<MindMap> { backStackEntry ->
            val route = backStackEntry.toRoute<MindMap>()
            MindMapScreen(mindmapId = route.mindmapId ?: "")
        }

        composable<Quotes> { QuotesScreen() }

        composable<Library> {
            LibraryScreen(
                onNavigateToBookDetail = { bookId ->
                    navController.navigate(BookDetail(bookId = bookId))
                },
            )
        }

        composable<Search> {
            SearchScreen(
                onNavigateBack = { navController.popBackStack() },
            )
        }

        composable<StatisticsWeekly>(
            deepLinks = listOf(
                navDeepLink { uriPattern = "gureum://statistics/weekly" },
                navDeepLink { uriPattern = "app://statistics/weekly" },
            ),
        ) { StatisticsScreen(initialPreset = DateRangePreset.WEEK) }

        composable<StatisticsMonthly>(
            deepLinks = listOf(
                navDeepLink { uriPattern = "gureum://statistics/monthly" },
                navDeepLink { uriPattern = "app://statistics/monthly" },
            ),
        ) { StatisticsScreen(initialPreset = DateRangePreset.MONTH) }

        composable<StatisticsYearly>(
            deepLinks = listOf(
                navDeepLink { uriPattern = "gureum://statistics/yearly" },
                navDeepLink { uriPattern = "app://statistics/yearly" },
            ),
        ) { StatisticsScreen(initialPreset = DateRangePreset.YEAR) }

        composable<Timer> { backStackEntry ->
            val route = backStackEntry.toRoute<Timer>()
            TimerScreen(
                userBookId = route.userBookId,
                onExit = {
                    navController.popBackStack()
                    navController.navigate(BookDetail(bookId = route.userBookId)) {
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
        }

        composable<MyPage> {
            MyPageScreen(
                onNavigateToLogin = {
                    navController.navigate(Login) {
                        popUpTo(navController.graph.id) {
                            inclusive = true
                            saveState = false
                        }
                        launchSingleTop = true
                        restoreState = false
                    }
                },
                onNavigateToWithdraw = { userName ->
                    navController.navigate(Withdraw(userName = userName))
                },
            )
        }

        composable<BookDetail>(
            deepLinks = listOf(
                navDeepLink { uriPattern = "gureum://bookdetail/{bookId}" },
                navDeepLink { uriPattern = "app://bookdetail/{bookId}" },
                navDeepLink {
                    uriPattern =
                        "gureum://bookdetail/{bookId}?showAddQuote={showAddQuote}&showAddManualRecord={showAddManualRecord}"
                },
                navDeepLink {
                    uriPattern =
                        "app://bookdetail/{bookId}?showAddQuote={showAddQuote}&showAddManualRecord={showAddManualRecord}"
                },
            ),
        ) { backStackEntry ->
            val route = backStackEntry.toRoute<BookDetail>()
            BookDetailScreen(
                bookId = route.bookId,
                snackbarHostState = snackbarHostState,
                onNavigateToMindmap = { bookId, mindmapId ->
                    navController.navigate(MindMap(bookId = bookId, mindmapId = mindmapId))
                },
                onNavigateToTimer = { userBookId ->
                    navController.navigate(Timer(userBookId = userBookId))
                },
                onNavigateBack = { navController.popBackStack() },
                initialShowAddQuote = route.showAddQuote,
                initialShowAddManualRecord = route.showAddManualRecord,
            )
        }

        composable<Withdraw> { backStackEntry ->
            val route = backStackEntry.toRoute<Withdraw>()
            WithdrawScreen(
                userName = route.userName,
                onNavigateToLogin = {
                    navController.navigate(Login) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateBack = { navController.popBackStack() },
            )
        }
    }
}
