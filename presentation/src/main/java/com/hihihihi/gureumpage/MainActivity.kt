package com.hihihihi.gureumpage

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hihihihi.gureumpage.designsystem.components.GureumAppBar
import com.hihihihi.gureumpage.designsystem.theme.GureumPageTheme
import com.hihihihi.gureumpage.navigation.GureumBottomNavBar
import com.hihihihi.gureumpage.navigation.GureumNavGraph
import com.hihihihi.gureumpage.navigation.NavigationRoute
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GureumPageTheme {
                GureumPageApp()
            }
        }
    }
}

@Composable
fun GureumPageApp() {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val hideBottomBarRoutes = listOf(
        NavigationRoute.Login.route,
        NavigationRoute.OnBoarding.route,
        NavigationRoute.BookDetail.route,
        NavigationRoute.Timer.route,
        NavigationRoute.MindMap.route
    )

    Scaffold(
        topBar = {
            when (currentRoute) {
                NavigationRoute.Library.route -> GureumAppBar(title = "서재")
                NavigationRoute.Quotes.route -> GureumAppBar(title = "필사 목록")
                NavigationRoute.Statistics.route -> GureumAppBar(title = "통계")
                NavigationRoute.MyPage.route -> GureumAppBar(title = "마이페이지")
                NavigationRoute.MindMap.route -> GureumAppBar(navController, "마인드맵", true)
                NavigationRoute.Timer.route -> GureumAppBar(navController, "독서 타이머", true)
                NavigationRoute.BookDetail.route -> GureumAppBar(navController, "", true)
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            if (currentRoute != null && hideBottomBarRoutes.none { currentRoute.startsWith(it) })
                GureumBottomNavBar(navController = navController)
        }
    ) { innerPadding ->
        GureumNavGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding),
            snackbarHostState = snackbarHostState
        )
    }
}