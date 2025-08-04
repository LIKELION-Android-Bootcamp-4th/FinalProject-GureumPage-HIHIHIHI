package com.hihihihi.gureumpage.ui.library

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hihihihi.gureumpage.designsystem.theme.GureumPageTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTypography
import com.hihihihi.gureumpage.ui.library.component.BookItem
import com.hihihihi.gureumpage.ui.library.component.ToggleTab
import com.hihihihi.gureumpage.ui.library.model.Book


@Composable
fun LibraryScreen(
    userId: String = "iK4v1WW1ZX4gID2HueBi", // 테스트용 기본 유저 ID
    viewModel: LibraryViewModel = hiltViewModel() // Hilt -> viewModel
) {
    //현재 탭 상태 true면 읽기 전, false 면 읽은 책
    var isBeforeReading by remember { mutableStateOf(true) }
    //현재 책 상태에 맞게 필터링
    val filteredBooks = books.filter { it.isRead != isBeforeReading }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {

        //앱 바 ui확인용
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 12.dp)
        ) {
            Text(
                text = "서재",
                style = GureumTypography.headlineSmall,
                color = GureumTheme.colors.gray800,
                modifier = Modifier.align(Alignment.Center)
            )
        }


        //상단 탭
        ToggleTab(
            isBeforeReading = isBeforeReading,
            onToggle = { isBeforeReading = it}
        )
        //그리드로 책 목록 출력(3열)
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredBooks) { book ->
                BookItem(
                    book = book,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}


@Preview(name = "Light", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)

@Composable
fun PreviewLibraryScreen() {
    GureumPageTheme {
        LibraryScreen(books = dummyBooks)
    }
}
