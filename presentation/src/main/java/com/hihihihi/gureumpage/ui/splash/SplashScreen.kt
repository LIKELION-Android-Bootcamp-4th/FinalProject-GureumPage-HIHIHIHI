package com.hihihihi.gureumpage.ui.splash

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.hihihihi.gureumpage.designsystem.components.GureumLinearProgressBar
import com.hihihihi.gureumpage.designsystem.components.Medi12Text
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTypography
import com.hihihihi.gureumpage.navigation.NavigationRoute
import com.hihihihi.gureumpage.notification.ReminderScheduler

@Composable
fun SplashView(
    navController: NavHostController,
    viewModel: SplashViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    var askedOnce by rememberSaveable { mutableStateOf(false) }

    var proceed by rememberSaveable { mutableStateOf(false) }

    var kicked by rememberSaveable { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        proceed = true
        if (isGranted) Toast.makeText(context, "권한이 허용되었습니다.", Toast.LENGTH_SHORT).show()
        else Toast.makeText(context, "권한이 거부되었습니다.", Toast.LENGTH_SHORT).show()
    }

    var showProgress by remember { mutableStateOf(false) }

    var startAnimation by remember { mutableStateOf(false) }

    val alpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1200), label = "",
        finishedListener = {
            showProgress = true
            viewModel.checkNetworkAndProceed()
        }
    )

    val offsetY by animateDpAsState(
        targetValue = if (startAnimation) 0.dp else 40.dp,
        animationSpec = tween(durationMillis = 1200), label = ""
    )

    LaunchedEffect(Unit) {
        startAnimation = true
    }

    val andimatedProgress by animateFloatAsState(
        targetValue = uiState.progress,
        animationSpec = tween(durationMillis = 300), label = ""
    )

    LaunchedEffect(askedOnce) {
        if (Build.VERSION.SDK_INT >= 33) {
            val granted = ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted && !askedOnce) {
                askedOnce = true
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                return@LaunchedEffect
            }
        }
        proceed = true
    }

    // 권한 허용 후 네트워크 상태 확인
    LaunchedEffect(proceed) {
        if (proceed && !kicked) {
            kicked = true
            showProgress = true
        }

        // 디버깅용 - 알림 테스트 모두 완료 후 삭제 예정
//        if (proceed) {
//            WorkManager.getInstance(context).enqueue(
//                OneTimeWorkRequestBuilder<DailyReminderWorker>()
//                    .setInitialDelay(10, TimeUnit.SECONDS)
//                    .build()
//            )
//
//            WorkManager.getInstance(context)
//                .getWorkInfosByTag("daily-reminder-test")
//                .get()
//                .forEach { Log.d("WM", "id=${it.id} state=${it.state}") }
//        }

        // TODO 읽은 기록 추가 후 알림 오는지 테스트 필요
        if (proceed) ReminderScheduler.scheduleDaily(context, hour = 22, minute = 0)
    }

    LaunchedEffect(proceed, uiState.isLoading, uiState.navTarget) {
        if (proceed && !uiState.isLoading) {
            when (uiState.navTarget) {
                SplashViewModel.NavTarget.Loading -> Unit
                SplashViewModel.NavTarget.NoNetwork -> Unit
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
    }

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
                        Color(0xFFE1F5FE),
                        Color(0xFFFFFDE7)
                    )
                )
            )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text(
                "구름한장",
                style = GureumTypography.displayMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = GureumTheme.colors.gray900,
                modifier = Modifier
                    .offset(y = offsetY)
                    .graphicsLayer { this.alpha = alpha }
            )
        }

        if (uiState.navTarget == SplashViewModel.NavTarget.NoNetwork) {
            AlertDialog(
                onDismissRequest = { },
                title = { Text("네트워크 오류") },
                text = { Text("인터넷 연결이 필요합니다.\n연결 후 다시 시도해주세요.") },
                confirmButton = {
                    TextButton(onClick = {
                        (navController.context as? Activity)?.finish()
                    }) { Text("앱 종료") }
                }
            )
        }

        if (showProgress && uiState.isLoading && uiState.loadingMessage.isNotEmpty()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 100.dp)
                    .padding(16.dp)
            ) {
                Medi12Text(
                    uiState.loadingMessage,
                    style = GureumTypography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = GureumTheme.colors.gray900
                )
                Spacer(Modifier.height(8.dp))
                GureumLinearProgressBar(
                    progress = andimatedProgress,
                    height = 12,
                )
            }
        }
    }
}
