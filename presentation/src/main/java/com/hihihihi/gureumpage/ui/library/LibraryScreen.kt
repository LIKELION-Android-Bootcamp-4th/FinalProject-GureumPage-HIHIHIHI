package com.hihihihi.gureumpage.ui.library

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
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.hihihihi.domain.model.ReadingStatus
import com.hihihihi.gureumpage.designsystem.components.Medi16Text
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.navigation.NavigationRoute
import com.hihihihi.gureumpage.ui.library.component.BookItem
import kotlinx.coroutines.launch
import kotlin.collections.lastIndex
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.zIndex
import com.hihihihi.gureumpage.R
import com.hihihihi.gureumpage.designsystem.components.Semi18Text
import kotlin.math.abs

@Composable
fun LibraryScreen(
    navController: NavHostController,
    viewModel: LibraryViewModel = hiltViewModel()
) {
    val tabTitles = listOf("읽기 전", "읽는 중", "읽은 후")

    val pagerState = rememberPagerState(pageCount = { tabTitles.size })
    val scope = rememberCoroutineScope()

    val uiState by viewModel.uiState.collectAsState()

    val plannedBooks = uiState.books.filter { it.status == ReadingStatus.PLANNED }
    val readingBooks = uiState.books.filter { it.status == ReadingStatus.READING }
    val finishedBooks = uiState.books.filter { it.status == ReadingStatus.FINISHED }

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
                                            onClicked = {
                                                navController.navigate(
                                                    NavigationRoute.BookDetail.createRoute(
                                                        it
                                                    )
                                                )
                                            }
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
                                            onClicked = {
                                                navController.navigate(
                                                    NavigationRoute.BookDetail.createRoute(
                                                        it
                                                    )
                                                )
                                            }
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
                                            onClicked = {
                                                navController.navigate(
                                                    NavigationRoute.BookDetail.createRoute(
                                                        it
                                                    )
                                                )
                                            }
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