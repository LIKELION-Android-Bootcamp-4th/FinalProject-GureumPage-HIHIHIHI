package com.hihihihi.gureumpage.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
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
fun OnBoardingScreen(navController: NavHostController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("OnBoarding Screen")

        Button(
            onClick = {
                navController.navigate(NavigationRoute.Home.route)
            }
        ) {
            Text("홈으로 이동")
        }
    }
}