package com.hihihihi.gureumpage.ui.login

import android.annotation.SuppressLint
import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.hihihihi.gureumpage.R
import com.hihihihi.gureumpage.designsystem.components.Medi12Text
import com.hihihihi.gureumpage.designsystem.components.Medi14Text
import com.hihihihi.gureumpage.designsystem.components.Medi16Text
import com.hihihihi.gureumpage.designsystem.components.Semi16Text
import com.hihihihi.gureumpage.designsystem.theme.GureumPageTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTypography
import com.hihihihi.gureumpage.ui.login.components.SocialLoginButton

@SuppressLint("ContextCastToActivity")
@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val googleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        result.data?.let { intent ->
            viewModel.handleGoogleSignInResult(intent, navController)
        }
    }

    val activity = LocalContext.current as? Activity
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearError()
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
                            Color(0xFFB3E3F8),
                            Color(0xFFFFFDE7)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "구름한장",
                    style = GureumTypography.displayMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = GureumTheme.colors.gray900
                )

                Spacer(modifier = Modifier.height(16.dp))

                Semi16Text(
                    "한 장 한 장 쌓이는",
                    color = GureumTheme.colors.gray700,
                )

                Spacer(modifier = Modifier.height(4.dp))

                Semi16Text(
                    "나의 소중한 독서 기록",
                    color = GureumTheme.colors.point,
                )

                Spacer(modifier = Modifier.height(120.dp))


                SocialLoginButton(
                    text = "구글 로그인",
                    textColor = Color.Black,
                    iconResId = R.drawable.ic_google,
                    backgroundColor = Color.White,
                    enabled = !uiState.isLoading,
                    onClick = {
                        viewModel.googleLogin(context, googleLauncher)
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                SocialLoginButton(
                    text = "카카오 로그인",
                    textColor = Color.Black,
                    iconResId = R.drawable.ic_kakao,
                    backgroundColor = Color(0xFFFEE500),
                    enabled = !uiState.isLoading,

                    onClick = {
                        viewModel.kakaoLogin(navController)
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                SocialLoginButton(
                    text = "네이버 로그인",
                    textColor = Color.White,
                    iconResId = R.drawable.ic_naver,
                    backgroundColor = Color(0xFF03C75A),
                    enabled = !uiState.isLoading,
                    onClick = {
                        activity?.let { viewModel.naverLogin(it, navController) }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))
                Medi12Text(
                    "로그인하여 나만의 독서 여정을 시작해보세요 ✨",
                    color = GureumTheme.colors.gray600,
                )
            }
        }

        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            GureumTheme.background.color,
                            RoundedCornerShape(12.dp)
                        )
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(40.dp),
                            color = GureumTheme.colors.primary
                        )

                        if (uiState.loadingMessage?.isNotEmpty() == true) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Medi14Text(
                                text = uiState.loadingMessage!!,
                                color = GureumTheme.colors.gray700
                            )
                        }
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
        ) { data ->
            Snackbar(
                snackbarData = data,
                containerColor = GureumTheme.colors.systemRed,
                contentColor = Color.White
            )
        }

}
