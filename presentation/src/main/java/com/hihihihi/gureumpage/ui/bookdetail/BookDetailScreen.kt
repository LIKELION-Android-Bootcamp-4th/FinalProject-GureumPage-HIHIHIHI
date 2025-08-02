package com.hihihihi.gureumpage.ui.bookdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.hihihihi.gureumpage.navigation.NavigationRoute

@Composable
fun BookDetailScreen(
    bookId: String,
    navController: NavHostController,
    viewModel: BookDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()


    var content by remember { mutableStateOf("") }
    var pageNumber by remember { mutableStateOf("") }

    LaunchedEffect(uiState.addQuoteState.isSuccess) {
        if (uiState.addQuoteState.isSuccess) {
            content = ""
            pageNumber = ""
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
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

            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("내용") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = pageNumber,
                onValueChange = { pageNumber = it.filter { ch -> ch.isDigit() } },
                label = { Text("페이지 번호") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    viewModel.addQuote(bookId, content, pageNumber.toIntOrNull())
                },
                enabled = !uiState.addQuoteState.isLoading && content.isNotBlank()
            ) {
                if (uiState.addQuoteState.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White
                    )
                } else Text("필사 추가")
            }


        }
    }
}