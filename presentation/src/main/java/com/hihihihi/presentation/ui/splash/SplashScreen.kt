package com.hihihihi.presentation.ui.splash

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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hihihihi.presentation.designsystem.components.GureumLinearProgressBar
import com.hihihihi.presentation.designsystem.components.Medi12Text
import com.hihihihi.presentation.designsystem.theme.GureumPageTheme
import com.hihihihi.presentation.designsystem.theme.GureumTheme
import com.hihihihi.presentation.designsystem.theme.GureumTypography
import com.hihihihi.presentation.notification.reminder.ReminderScheduler
import com.hihihihi.presentation.notification.summary.SummaryScheduler

@Composable
fun SplashView(
    onNavigateToLogin: () -> Unit,
    onNavigateToOnBoarding: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToWidget: (Any) -> Unit,
    viewModel: SplashViewModel = hiltViewModel(),
    pendingWidgetRoute: Any? = null,
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var showProgress by remember { mutableStateOf(false) }
    var startAnimation by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        viewModel.onPermissionResult()
        if (isGranted) Toast.makeText(context, "권한이 허용되었습니다.", Toast.LENGTH_SHORT).show()
        else Toast.makeText(context, "권한이 거부되었습니다.", Toast.LENGTH_SHORT).show()
    }

    val alpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1200), label = "",
        finishedListener = {
            showProgress = true
            viewModel.checkNetworkAndProceed()
        },
    )

    val offsetY by animateDpAsState(
        targetValue = if (startAnimation) 0.dp else 40.dp,
        animationSpec = tween(durationMillis = 1200), label = "",
    )

    LaunchedEffect(pendingWidgetRoute) {
        if (pendingWidgetRoute != null) {
            viewModel.setPendingWidgetRoute(pendingWidgetRoute)
        }
    }

    LaunchedEffect(Unit) {
        startAnimation = true
    }

    val animatedProgress by animateFloatAsState(
        targetValue = uiState.progress,
        animationSpec = tween(durationMillis = 300), label = "",
    )

    LaunchedEffect(uiState.permissionAsked) {
        if (Build.VERSION.SDK_INT >= 33) {
            val granted = ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted && !uiState.permissionAsked) {
                viewModel.markPermissionAsked()
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                return@LaunchedEffect
            }
        }
        viewModel.onPermissionResult()
    }

    LaunchedEffect(uiState.permissionHandled) {
        if (uiState.permissionHandled && !uiState.schedulersSetUp) {
            viewModel.markSchedulersSetUp()
            showProgress = true

            ReminderScheduler.scheduleDaily(context, hour = 22, minute = 0)
            SummaryScheduler.scheduleWeekly(context)
            SummaryScheduler.scheduleMonthly(context)
            SummaryScheduler.scheduleYearly(context)
        }
    }

    val navEvent by remember(uiState) {
        derivedStateOf {
            if (uiState.permissionHandled && !uiState.isLoading) uiState.navTarget else null
        }
    }

    LaunchedEffect(navEvent) {
        when (val target = navEvent ?: return@LaunchedEffect) {
            SplashViewModel.NavTarget.Login -> onNavigateToLogin()
            SplashViewModel.NavTarget.Onboarding -> onNavigateToOnBoarding()
            SplashViewModel.NavTarget.Home -> onNavigateToHome()
            is SplashViewModel.NavTarget.Widget -> onNavigateToWidget(target.route)
            else -> {
                // Loading, NoNetwork 상태는 별도 UI에서 처리
            }
        }
    }

    SplashContent(
        isNoNetwork = uiState.navTarget == SplashViewModel.NavTarget.NoNetwork,
        isLoading = uiState.isLoading,
        loadingMessage = uiState.loadingMessage,
        showProgress = showProgress,
        alpha = alpha,
        offsetY = offsetY,
        animatedProgress = animatedProgress,
        onExit = { (context as? Activity)?.finish() },
    )
}

@Composable
private fun SplashContent(
    isNoNetwork: Boolean,
    isLoading: Boolean,
    loadingMessage: String,
    showProgress: Boolean,
    alpha: Float,
    offsetY: Dp,
    animatedProgress: Float,
    onExit: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = if (GureumTheme.isDarkTheme) listOf(
                        GureumTheme.colors.background,
                        Color(0xFF00153F),
                    ) else listOf(
                        Color(0xFF51C1F6),
                        Color(0xFFE1F5FE),
                        Color(0xFFFFFDE7),
                    ),
                ),
            ),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.align(Alignment.Center),
        ) {
            Text(
                "구름한장",
                style = GureumTypography.displayMedium.copy(
                    fontWeight = FontWeight.Bold,
                ),
                color = GureumTheme.colors.gray900,
                modifier = Modifier
                    .offset(y = offsetY)
                    .graphicsLayer { this.alpha = alpha },
            )
        }

        if (isNoNetwork) {
            AlertDialog(
                onDismissRequest = { },
                title = { Text("네트워크 오류") },
                text = { Text("인터넷 연결이 필요합니다.\n연결 후 다시 시도해주세요.") },
                containerColor = GureumTheme.colors.card,
                confirmButton = {
                    TextButton(onClick = onExit) { Text("앱 종료") }
                },
            )
        }

        if (showProgress && isLoading && loadingMessage.isNotEmpty()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 100.dp)
                    .padding(16.dp),
            ) {
                Medi12Text(
                    loadingMessage,
                    style = GureumTypography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                    color = GureumTheme.colors.gray900,
                )
                Spacer(Modifier.height(8.dp))
                GureumLinearProgressBar(
                    progress = animatedProgress,
                    height = 12,
                )
            }
        }
    }
}

@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark")
@Composable
private fun SplashPreview() {
    GureumPageTheme {
        SplashContent(
            isNoNetwork = false,
            isLoading = true,
            loadingMessage = "데이터를 불러오는 중...",
            showProgress = true,
            alpha = 1f,
            offsetY = 0.dp,
            animatedProgress = 0.6f,
            onExit = {},
        )
    }
}
