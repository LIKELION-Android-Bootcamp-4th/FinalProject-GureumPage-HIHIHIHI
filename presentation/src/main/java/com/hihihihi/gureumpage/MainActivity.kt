package com.hihihihi.gureumpage

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hihihihi.domain.model.GureumThemeType
import com.hihihihi.domain.usecase.user.GetThemeFlowUseCase
import com.hihihihi.gureumpage.designsystem.components.GureumAppBar
import com.hihihihi.gureumpage.designsystem.theme.GureumPageTheme
import com.hihihihi.gureumpage.navigation.BottomNavItem
import com.hihihihi.gureumpage.navigation.GureumBottomNavBar
import com.hihihihi.gureumpage.navigation.GureumNavGraph
import com.hihihihi.gureumpage.navigation.NavigationRoute
import com.hihihihi.gureumpage.ui.timer.LocalAppBarUpClick
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @HiltViewModel
    class GureumThemeViewModel @Inject constructor(getTheme: GetThemeFlowUseCase) : ViewModel() {
        val theme = getTheme().stateIn(viewModelScope, SharingStarted.Lazily, GureumThemeType.DARK)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel = hiltViewModel<GureumThemeViewModel>()
            val currentTheme by viewModel.theme.collectAsState()
            // 모드 상태에 따라 GureumPageTheme 에 반영
            GureumPageTheme(
                darkTheme = when (currentTheme) {
                    GureumThemeType.LIGHT -> false
                    else -> true
                }
            ) { GureumPageApp() }
        }
    }
}

@Composable
fun GureumPageApp() {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val hideBottomBarRoutes = listOf(
        NavigationRoute.Login.route,
        NavigationRoute.OnBoarding.route,
        NavigationRoute.BookDetail.route,
        NavigationRoute.Timer.route,
        NavigationRoute.MindMap.route,
        NavigationRoute.Withdraw.route,
        NavigationRoute.Search.route,
        NavigationRoute.Splash.route
    )

    var timerAppbarUp by remember { mutableStateOf(0L) }

    LaunchedEffect(currentRoute) {
        if (currentRoute != NavigationRoute.Timer.route) {
            timerAppbarUp = 0L
        }
    }

    BackHandler {
        val currentRoute = navController.currentBackStackEntry?.destination?.route

        when {
            // 현재 루트가 홈이면 앱 종료
            currentRoute == NavigationRoute.Home.route -> {
                (context as? Activity)?.finish()
            }

            // 현재 루트가 바텀 내비 아이템이면 홈으로 이동
            BottomNavItem.items.any { it.route == currentRoute } -> {
                navController.navigate(NavigationRoute.Home.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }

            else -> {
                navController.popBackStack()
            }
        }
    }

    Scaffold(
        topBar = {
            when (currentRoute) {
                NavigationRoute.Library.route -> GureumAppBar(title = "서재")
                NavigationRoute.Quotes.route -> GureumAppBar(title = "필사 목록")
                NavigationRoute.Statistics.route -> GureumAppBar(title = "통계")
                NavigationRoute.MyPage.route -> GureumAppBar(title = "마이페이지")
                NavigationRoute.MindMap.route -> GureumAppBar(navController, "마인드맵", true)
                NavigationRoute.Timer.route -> GureumAppBar(
                    navController = navController,
                    title = "독서 스톱워치",
                    showUpButton = true,
                    onUpClick = {
                        timerAppbarUp = System.currentTimeMillis()
                    }
                )

                NavigationRoute.BookDetail.route -> GureumAppBar(navController, "", true)
                NavigationRoute.Withdraw.route -> GureumAppBar(navController, "계정 탈퇴", true)
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            if (currentRoute != null && hideBottomBarRoutes.none { currentRoute.startsWith(it) })
                GureumBottomNavBar(navController = navController)
        }
    ) { innerPadding ->
        CompositionLocalProvider(LocalAppBarUpClick provides timerAppbarUp) {
            GureumNavGraph(
                navController = navController,
                modifier = Modifier.padding(innerPadding),
                snackbarHostState = snackbarHostState
            )
        }
    }
}