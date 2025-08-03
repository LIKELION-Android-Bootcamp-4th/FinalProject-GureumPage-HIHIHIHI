package com.hihihihi.gureumpage.ui.library

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hihihihi.gureumpage.designsystem.theme.GureumColors
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.ui.library.component.BookItem
import com.hihihihi.gureumpage.ui.library.component.ToggleTab
import com.hihihihi.gureumpage.ui.library.model.Book

@Preview(name = "Light", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen() {
    var isBeforeReading by remember { mutableStateOf(true) }

    val dummyBooks = remember {
        listOf(
            Book("1", "책1", "작가1", "https://via.placeholder.com/150", true),
            Book("2", "책2", "작가2", "https://via.placeholder.com/150", false),
            Book("3", "책3", "작가3", "https://via.placeholder.com/150", true),
            Book("4", "책4", "작가4", "https://via.placeholder.com/150", false)
        )
    }

    val filteredBooks = dummyBooks.filter { it.isRead != isBeforeReading }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "서재",
                        style = MaterialTheme.typography.titleLarge,
                        color = GureumTheme.colors.gray800
                    )
                },
                modifier = Modifier.height(56.dp),
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    titleContentColor = GureumTheme.colors.gray800,
                    containerColor = GureumTheme.colors.card
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            ToggleTab(
                isBeforeReading = isBeforeReading,
                onToggle = { isBeforeReading = it }
            )

            //책 목록
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredBooks) { book ->
                    BookItem(book = book)
                }
            }
        }
    }
}