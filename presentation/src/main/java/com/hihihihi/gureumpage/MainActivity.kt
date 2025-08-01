package com.hihihihi.gureumpage

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.hihihihi.data.ForTest
import com.hihihihi.domain.Book
import com.hihihihi.gureumpage.designsystem.theme.GureumPageTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTypography
import com.hihihihi.gureumpage.navigation.GureumBottomNavBar
import com.hihihihi.gureumpage.navigation.GureumNavGraph

class MainActivity : ComponentActivity() {
    val book = Book("test")
    val test = ForTest()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GureumPageTheme {
                GureumPageScreen()
            }
        }
    }
}

@Composable
fun GureumPageScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            GureumBottomNavBar(navController = navController)
        }
    ) { innerPadding ->
        GureumNavGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}