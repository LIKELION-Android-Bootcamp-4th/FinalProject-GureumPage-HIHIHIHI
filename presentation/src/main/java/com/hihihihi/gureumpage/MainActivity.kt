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
import com.hihihihi.data.ForTest
import com.hihihihi.domain.Book
import com.hihihihi.gureumpage.designsystem.theme.GureumPageTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTypography

class MainActivity : ComponentActivity() {
    val book = Book("test")
    val test = ForTest()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GureumPageTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Row(modifier = Modifier.fillMaxSize()) {
        Icon(painter = painterResource(id = R.drawable.ic_tooltip), contentDescription = null)
    Text(
        text = "Hello $name!",
        modifier = modifier,
        style = GureumTypography.displayLarge
    )
}}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GureumPageTheme {
        Greeting("Android")
    }
}