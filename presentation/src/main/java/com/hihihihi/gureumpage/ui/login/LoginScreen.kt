package com.hihihihi.gureumpage.ui.login

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.hihihihi.gureumpage.R
import com.hihihihi.gureumpage.ui.login.components.SocialLoginButton



@SuppressLint("ContextCastToActivity")
@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val googleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        result.data?.let { intent ->
            viewModel.handleGoogleSignInResult(intent, navController)
        }
    }

    val activity = LocalContext.current as? Activity

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Login Screen", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(32.dp))

        // 🔵 Google
        SocialLoginButton(
            text = "구글 로그인",
            textColor = Color.Black,
            iconResId = R.drawable.ic_google,
            backgroundColor = Color.White,
            onClick = {
                viewModel.googleLogin(context, googleLauncher)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 🟡 Kakao
        SocialLoginButton(
            text = "카카오 로그인",
            textColor = Color.Black,
            iconResId = R.drawable.ic_kakao,
            backgroundColor = Color(0xFFFEE500), // Kakao Yellow
            onClick = {
                viewModel.kakaoLogin(navController)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 🟢 Naver
        SocialLoginButton(
            text = "네이버 로그인",
            textColor = Color.White,
            iconResId = R.drawable.ic_naver,
            backgroundColor = Color(0xFF03C75A), // Naver Green
            onClick = {
                activity?.let {
                    viewModel.naverLogin(it, navController)
                } ?: Log.e("LoginScreen", "Activity가 null입니다.")
            }
        )
    }
}
