package com.hihihihi.gureumpage.ui.bookdetail

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
fun BookDetailScreen(
    bookId: String,
    navController: NavHostController
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("BookDetail Screen")

        Button(
            onClick = {
                navController.navigate(NavigationRoute.Timer.route)
            }
        ) {
            Text("타이머로 이동")
        }
        Button(
            onClick = {
                navController.navigate(NavigationRoute.MindMap.route)
            }
        ) {
            Text("마인드맵 이동")
        }
    }
}