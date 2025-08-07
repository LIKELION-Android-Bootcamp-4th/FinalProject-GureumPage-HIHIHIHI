package com.hihihihi.gureumpage

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hihihihi.domain.usecase.theme.GetDarkThemeUserCase
import com.hihihihi.gureumpage.designsystem.theme.GureumPageTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTypography
import com.hihihihi.gureumpage.navigation.GureumBottomNavBar
import com.hihihihi.gureumpage.navigation.GureumNavGraph
import com.hihihihi.gureumpage.navigation.NavigationRoute
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    //다크모드 조회용 UseCase
    @Inject lateinit var getDarkThemeUserCase: GetDarkThemeUserCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            //DataStore에서 다크모드 여부 상태 가져오기
            val isDarkTheme by getDarkThemeUserCase().collectAsState(initial = false)

            // 모드 상태에 따라 GureumPageTheme 에 반영
            GureumPageTheme(darkTheme = isDarkTheme) {
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
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            if(currentRoute != null && hideBottomBarRoutes.none{currentRoute.startsWith(it)})
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