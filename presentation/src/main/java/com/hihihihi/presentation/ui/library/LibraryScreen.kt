package com.hihihihi.presentation.ui.library

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hihihihi.domain.model.ReadingStatus
import com.hihihihi.presentation.R
import com.hihihihi.presentation.designsystem.components.Medi16Text
import com.hihihihi.presentation.designsystem.components.Semi18Text
import com.hihihihi.presentation.designsystem.theme.GureumPageTheme
import com.hihihihi.presentation.designsystem.theme.GureumTheme
import com.hihihihi.presentation.ui.library.component.BookItem
import com.hihihihi.presentation.ui.model.UserBookUiModel
import kotlinx.coroutines.launch
import kotlin.collections.filter
import kotlin.math.abs

@Composable
fun LibraryScreen(
    onNavigateToBookDetail: (String) -> Unit,
    viewModel: LibraryViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    when (val state = uiState) {
        LibraryUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = GureumTheme.colors.primary)
            }
        }

        is LibraryUiState.Error -> {
            LibraryContent(
                books = state.previous?.books.orEmpty(),
                loadErrorMessage = state.message,
                onNavigateToBookDetail = onNavigateToBookDetail,
            )
        }

        is LibraryUiState.Content -> {
            LibraryContent(
                books = state.books,
                loadErrorMessage = null,
                onNavigateToBookDetail = onNavigateToBookDetail,
            )
        }
    }
}

@Composable
private fun LibraryContent(
    books: List<UserBookUiModel>,
    loadErrorMessage: String?,
    onNavigateToBookDetail: (String) -> Unit,
) {
    val tabTitles = listOf("읽기 전", "읽는 중", "읽은 후")
    val pagerState = rememberPagerState(pageCount = { tabTitles.size })
    val scope = rememberCoroutineScope()

    val plannedBooks = books.filter { it.status == ReadingStatus.PLANNED }
    val readingBooks = books.filter { it.status == ReadingStatus.READING }
    val finishedBooks = books.filter { it.status == ReadingStatus.FINISHED }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(16.dp))

            TabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = GureumTheme.colors.card,
                contentColor = GureumTheme.colors.primary,
                indicator = { tabPositions ->
                    SlidingPillIndicator(tabPositions, pagerState)
                },
                divider = {},
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(GureumTheme.colors.card)
                    .padding(4.dp)
            ) {
                tabTitles.forEachIndexed { index, text ->
                    val isSelected = pagerState.currentPage == index

                    Tab(
                        selected = isSelected,
                        modifier = Modifier
                            .zIndex(1f)
                            .clip(RoundedCornerShape(14.dp)),
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        selectedContentColor = GureumTheme.colors.white,
                        unselectedContentColor = GureumTheme.colors.gray300,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(vertical = 12.dp)
                        ) {
                            Icon(
                                painter = painterResource(
                                    id = when (index) {
                                        0 -> R.drawable.ic_library_planned
                                        1 -> R.drawable.ic_library_reading
                                        else -> R.drawable.ic_library_finished
                                    }
                                ),
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (isSelected) GureumTheme.colors.white
                                else GureumTheme.colors.gray300
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Medi16Text(
                                text = text,
                                color = if (isSelected) GureumTheme.colors.white
                                else GureumTheme.colors.gray300
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Box(modifier = Modifier.fillMaxSize()) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    if (loadErrorMessage != null) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Semi18Text(
                                "불러올 수 없습니다",
                                color = GureumTheme.colors.gray500
                            )
                            Spacer(Modifier.height(16.dp))
                            Medi16Text(
                                "잠시 후 다시 시도해주세요.",
                                color = GureumTheme.colors.gray400
                            )
                        }
                    } else {
                        when (page) {
                            0 -> {
                                if (plannedBooks.isEmpty()) {
                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Semi18Text(
                                            "아직 담은 책이 없어요",
                                            color = GureumTheme.colors.gray500
                                        )
                                        Spacer(Modifier.height(16.dp))
                                        Medi16Text(
                                            "읽고 싶은 책을 추가해 보세요.",
                                            color = GureumTheme.colors.gray400
                                        )
                                    }
                                } else {
                                    LazyVerticalGrid(
                                        columns = GridCells.Fixed(3),
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(horizontal = 16.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        contentPadding = PaddingValues(top = 18.dp)
                                    ) {
                                        items(plannedBooks) { book ->
                                            BookItem(
                                                book = book,
                                                onClicked = { onNavigateToBookDetail(it) }
                                            )
                                        }
                                    }
                                }
                            }

                            1 -> {
                                if (readingBooks.isEmpty()) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(horizontal = 16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Semi18Text(
                                            "읽고 있는 책이 없어요",
                                            color = GureumTheme.colors.gray500
                                        )
                                        Spacer(Modifier.height(16.dp))
                                        Medi16Text(
                                            "책을 찾아 추가하고 새로운 독서를 시작해 보세요.",
                                            color = GureumTheme.colors.gray400

                                        )
                                    }
                                } else {
                                    LazyVerticalGrid(
                                        columns = GridCells.Fixed(3),
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(horizontal = 16.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        contentPadding = PaddingValues(top = 18.dp)
                                    ) {
                                        items(readingBooks) { book ->
                                            BookItem(
                                                book = book,
                                                onClicked = { onNavigateToBookDetail(it) }
                                            )
                                        }
                                    }
                                }
                            }

                            2 -> {
                                if (finishedBooks.isEmpty()) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(horizontal = 16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Semi18Text(
                                            "아직 완독한 책이 없어요",
                                            color = GureumTheme.colors.gray500
                                        )
                                        Spacer(Modifier.height(16.dp))
                                        Medi16Text(
                                            "첫 완독을 기록하면 서재가 채워져요.",
                                            color = GureumTheme.colors.gray400
                                        )
                                    }
                                } else {

                                    LazyVerticalGrid(
                                        columns = GridCells.Fixed(3),
                                        modifier = Modifier.fillMaxSize(),
                                        verticalArrangement = Arrangement.spacedBy(12.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        contentPadding = PaddingValues(top = 18.dp)
                                    ) {
                                        items(finishedBooks) { book ->
                                            BookItem(
                                                book = book,
                                                onClicked = { onNavigateToBookDetail(it) }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 위쪽 그래디언트 오버레이
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(16.dp)
                            .align(Alignment.TopCenter)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        GureumTheme.colors.background,
                                        Color.Transparent
                                    ),
                                )
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun SlidingPillIndicator(
    positions: List<TabPosition>,
    pagerState: androidx.compose.foundation.pager.PagerState,
    widthFraction: Float = 1f,
    height: Dp = 48.dp // 인디케이터 두께
) {
    val curr = pagerState.currentPage
    val off = pagerState.currentPageOffsetFraction
    val next = (curr + if (off >= 0f) 1 else -1).coerceIn(0, positions.lastIndex)

    val start = positions[curr]
    val end = positions[next]
    val fraction = abs(off)

    val tabLeft = lerp(start.left, end.left, fraction)
    val tabWidth = lerp(start.width, end.width, fraction)
    val pillWidth = tabWidth * widthFraction
    val pillLeft = tabLeft + (tabWidth - pillWidth) / 2

    Box(Modifier.fillMaxSize()) {
        Box(
            Modifier
                .align(Alignment.CenterStart)   // 세로 중앙
                .offset(x = pillLeft)           // 가로 위치
                .width(pillWidth)               // 가로 길이 축소
                .height(height)                 // 두께 줄이기
                .clip(RoundedCornerShape(14.dp))
                .background(GureumTheme.colors.primary)
                .zIndex(-1f)
        )
    }
}

@Preview(name = "Empty - Light", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Empty - Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun LibraryEmptyPreview() {
    GureumPageTheme {
        LibraryContent(books = emptyList(), loadErrorMessage = null, onNavigateToBookDetail = {})
    }
}

@Preview(name = "WithBooks - Light", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "WithBooks - Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun LibraryWithBooksPreview() {
    val sampleBooks = listOf(
        UserBookUiModel(
            userBookId = "1",
            title = "데미안",
            author = "헤르만 헤세",
            imageUrl = "",
            status = ReadingStatus.PLANNED,
            currentPage = 0,
            totalPage = 250,
            startDate = null,
            endDate = null,
            totalReadTime = 0,
            rating = null,
            review = null,
            category = "소설",
            publisher = "민음사",
            isbn13 = "1234567890123",
            description = "나를 찾아가는 길"
        ),
        UserBookUiModel(
            userBookId = "2",
            title = "코틀린 입문",
            author = "작가 미상",
            imageUrl = "",
            status = ReadingStatus.READING,
            currentPage = 50,
            totalPage = 400,
            startDate = null,
            endDate = null,
            totalReadTime = 120,
            rating = null,
            review = null,
            category = "IT",
            publisher = "출판사",
            isbn13 = "9876543210987",
            description = "코틀린은 즐거워"
        ),
        UserBookUiModel(
            userBookId = "3",
            title = "클린 아키텍처",
            author = "로버트 C. 마틴",
            imageUrl = "",
            status = ReadingStatus.FINISHED,
            currentPage = 350,
            totalPage = 350,
            startDate = null,
            endDate = null,
            totalReadTime = 600,
            rating = 5.0,
            review = "훌륭한 책입니다.",
            category = "IT",
            publisher = "인사이트",
            isbn13 = "1122334455667",
            description = "소프트웨어 구조에 대하여"
        )
    )
    GureumPageTheme {
        LibraryContent(books = sampleBooks, loadErrorMessage = null, onNavigateToBookDetail = {})
    }
}
