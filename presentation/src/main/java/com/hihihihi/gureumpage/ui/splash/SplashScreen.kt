package com.hihihihi.gureumpage.ui.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.hihihihi.gureumpage.designsystem.theme.GureumTypography
import com.hihihihi.gureumpage.navigation.NavigationRoute

@Composable
fun SplashView(
    navController: NavHostController,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val target by viewModel.nav.collectAsState()

    LaunchedEffect(target) {
        when (target) {
            SplashViewModel.NavTarget.Loading -> Unit
            SplashViewModel.NavTarget.Login -> {
                navController.navigate(NavigationRoute.Login.route) {
                    popUpTo(NavigationRoute.Splash.route) { inclusive = true }
                    launchSingleTop = true
                }
            }

            SplashViewModel.NavTarget.Onboarding -> {
                navController.navigate(NavigationRoute.OnBoarding.route) {
                    popUpTo(NavigationRoute.Splash.route) { inclusive = true }
                    launchSingleTop = true
                }
            }

            SplashViewModel.NavTarget.Home -> {
                navController.navigate(NavigationRoute.Home.route) {
                    popUpTo(NavigationRoute.Splash.route) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    }

    // 임시 스플래쉬
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("구름한장", style = GureumTypography.displayLarge)
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
        }
    }
}
