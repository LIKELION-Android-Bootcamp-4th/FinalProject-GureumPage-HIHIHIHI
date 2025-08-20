package com.hihihihi.gureumpage.ui.splash

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.hihihihi.gureumpage.R
import com.hihihihi.gureumpage.designsystem.theme.GureumPageTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTypography
import com.hihihihi.gureumpage.navigation.NavigationRoute
import kotlinx.coroutines.delay

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

    var startAnimation by remember { mutableStateOf(false) }

    val alpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1200), label = ""
    )

    val offsetY by animateDpAsState(
        targetValue = if (startAnimation) 0.dp else 40.dp,
        animationSpec = tween(durationMillis = 1200), label = ""
    )

    LaunchedEffect(Unit) {
        startAnimation = true
    }


    // 임시 스플래쉬
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = if (GureumTheme.isDarkTheme) listOf(
                        GureumTheme.colors.background,
                        Color(0xFF00153F)
                    ) else listOf(
                        Color(0xFF51C1F6),
                        Color(0xFFB3E3F8),
                        Color(0xFFFFFDE7)
                    )
                )
            )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text(
                "구름한장",
                style = GureumTypography.displayMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = GureumTheme.colors.white,
                modifier = Modifier
                    .offset(y = offsetY)
                    .graphicsLayer { this.alpha = alpha }
            )
        }
    }
}
