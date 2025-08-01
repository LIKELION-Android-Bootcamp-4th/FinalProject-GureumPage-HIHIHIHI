package com.hihihihi.gureumpage.ui.home

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
fun HomeScreen(navController: NavHostController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Home Screen")

        Button(
            onClick = {
                navController.navigate(NavigationRoute.BookDetail.createRoute("123"))  // TODO 123은 임시 고정 ID 입니다. 추후 실제 ID 값 넘기기
            }
        ) {
            Text("책 상세로 이동")
        }
    }
}