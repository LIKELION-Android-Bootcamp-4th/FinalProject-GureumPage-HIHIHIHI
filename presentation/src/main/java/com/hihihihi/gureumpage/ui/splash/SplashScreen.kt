package com.hihihihi.gureumpage.ui.splash

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.hihihihi.gureumpage.designsystem.theme.GureumTypography
import com.hihihihi.gureumpage.navigation.NavigationRoute
import com.kakao.sdk.common.util.Utility

@Composable
fun SplashView(navController: NavHostController) {
    LaunchedEffect(Unit) {
//        FirebaseAuth.getInstance().signOut()

        val isLoggedIn = FirebaseAuth.getInstance().currentUser != null
        Log.e("TAG", "SplashView: 여기 진입 $isLoggedIn", )
        //Log.e("TAG", "SplashView: ${FirebaseAuth.getInstance().currentUser!!.uid}", )

        navController.navigate(
            if (isLoggedIn) {
                Log.e("TAG", "SplashView: 로그인 되어있음 $isLoggedIn")
                NavigationRoute.Home.route
            }
            else {
                Log.e("TAG", "SplashView: 로그인 안되있음 $isLoggedIn")
                NavigationRoute.Login.route
            }
        ) {
            popUpTo(NavigationRoute.Splash.route) { inclusive = true }
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
