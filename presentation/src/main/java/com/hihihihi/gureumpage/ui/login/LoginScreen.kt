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
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.user.UserApiClient



@SuppressLint("ContextCastToActivity")
@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    var kakaoToken by remember { mutableStateOf<String?>(null) }
    var naverToken by remember { mutableStateOf<String?>(null) }

    val googleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        result.data?.let { intent ->
            viewModel.handleGoogleSignInResult(intent, navController)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Login Screen")

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            viewModel.googleLogin(context, googleLauncher)
        }) {
            Text("구글 로그인")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            viewModel.kakaoLogin(navController)
        }) {
            Text("카카오 로그인")
        }


        Spacer(modifier = Modifier.height(16.dp))

        val activity = LocalContext.current as? Activity

        Button(onClick = {
            activity?.let {
                viewModel.naverLogin(it, navController)
            } ?: run {
                // activity가 null일 때 처리 (토스트 띄우기 등)
                Log.e("LoginScreen", "Activity가 null입니다.")
            }

        }) {
            Text("네이버 로그인 (토큰 전달 시 실행)")
        }
    }
}
