package com.hihihihi.gureumpage.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.hihihihi.gureumpage.navigation.NavigationRoute

@Composable
fun LoginScreen(navController: NavHostController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Login Screen")

        Button(
            onClick = {
                navController.navigate(NavigationRoute.OnBoarding.route)
            }
        ) {
            Text("온보딩으로 이동")
        }
    }
}