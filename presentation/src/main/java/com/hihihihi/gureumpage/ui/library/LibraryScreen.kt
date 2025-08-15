package com.hihihihi.gureumpage.ui.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.hihihihi.domain.model.ReadingStatus
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTypography
import com.hihihihi.gureumpage.navigation.NavigationRoute
import com.hihihihi.gureumpage.ui.library.component.BookItem
import com.hihihihi.gureumpage.ui.library.component.ToggleTab

@Composable
fun LibraryScreen(
    navController: NavHostController,
    viewModel: LibraryViewModel = hiltViewModel() // Hilt -> viewModel
) {
    //현재 탭 상태 true면 읽기 전, false 면 읽은 책
    var isBeforeReading by remember { mutableStateOf(true) }

    //viewModel 에서 책 리시트 가져옴
    val uiState by viewModel.uiState.collectAsState()

    //현재 책 상태에 맞게 필터링
    val filteredBooks = uiState.books.filter {
        if (isBeforeReading) it.status != ReadingStatus.FINISHED
        else it.status == ReadingStatus.FINISHED
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(Modifier.height(16.dp))

        //상단 탭
        ToggleTab(
            isBeforeReading = isBeforeReading,
            onToggle = { isBeforeReading = it }
        )
        //ui 상태 분기
        when {
            //로딩 중
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = GureumTheme.colors.primary)
                }
            }
            //에러 발생
            uiState.errorMessage != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "에러 발생", color = GureumTheme.colors.systemRed)
                }
            }
            //필터링된 책이 없을 경우
            filteredBooks.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "표시할 책이 없습니다.", style = GureumTypography.bodyMedium)
                }
            }
            //정상적으로 작동될 떄
            else -> {
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
                            onClicked = {navController.navigate(NavigationRoute.BookDetail.createRoute(it))}
                        )
                    }
                }
            }
        }
    }
}

