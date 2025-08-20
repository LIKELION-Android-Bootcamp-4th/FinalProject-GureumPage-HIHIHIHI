package com.hihihihi.gureumpage.ui.home.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.hihihihi.domain.model.UserBook
import com.hihihihi.gureumpage.R
import com.hihihihi.gureumpage.common.utils.formatDateToSimpleString
import com.hihihihi.gureumpage.common.utils.formatSecondsToReadableTime
import com.hihihihi.gureumpage.common.utils.pxToDp
import com.hihihihi.gureumpage.designsystem.components.BookCoverImage
import com.hihihihi.gureumpage.designsystem.components.GureumCard
import com.hihihihi.gureumpage.designsystem.components.GureumLinearProgressBar
import com.hihihihi.gureumpage.designsystem.components.Medi14Text
import com.hihihihi.gureumpage.designsystem.components.Semi16Text
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTypography
import kotlin.math.absoluteValue

@SuppressLint("RestrictedApi", "UnusedBoxWithConstraintsScope")
@Composable
fun CurrentReadingBookSection(
    books: List<UserBook>,
    onBookClick: (String) -> Unit,
    onAddBookClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(GureumTheme.colors.background),
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(top = 8.dp)
        ) {
            Semi16Text("독서중인 책", isUnderline = true)
        }

        Spacer(Modifier.height(10.dp))

        if (books.isEmpty()) {
            EmptyReadingBooksCard(onAddBookClick)
        } else {
            ReadingBooksPager(
                books = books,
                onBookClick = onBookClick
            )
        }

        // 아래 그림자 끊기는 것 같아 추가
        Spacer(Modifier.height(10.dp))
    }

}


@Composable
fun EmptyReadingBooksCard(
    onAddBookClick: () -> Unit
) {
    GureumCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        onClick = onAddBookClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_plus),
                contentDescription = "책 추가",
                tint = GureumTheme.colors.gray300,
                modifier = Modifier.size(32.dp)
            )

            Spacer(Modifier.height(20.dp))

            Semi16Text("읽고 있는 책이 없어요!")
            Spacer(Modifier.height(4.dp))
            Medi14Text("새로운 책을 추가해서 독서를 시작해보세요.", color = GureumTheme.colors.gray700)
        }
    }
}

@Composable
private fun ReadingBooksPager(
    books: List<UserBook>,
    onBookClick: (String) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { books.size })
    val contentPadding = 30.dp
    val pageSpacing = 16.dp
    val scaleSizeRatio = 0.8f

    HorizontalPager(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        state = pagerState,
        key = { books[it].userBookId },
        contentPadding = PaddingValues(horizontal = contentPadding),
        pageSpacing = pageSpacing,
    ) { page ->
        val book = books[page]
        ReadingBookCard(
            book = book,
            pagerState = pagerState,
            page = page,
            scaleSizeRatio = scaleSizeRatio,
            onBookClick = onBookClick
        )
    }
}


@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
private fun ReadingBookCard(
    book: UserBook,
    pagerState: androidx.compose.foundation.pager.PagerState,
    page: Int,
    scaleSizeRatio: Float,
    onBookClick: (String) -> Unit
) {
    BoxWithConstraints {
        val maxWidthDp = pxToDp(constraints.maxWidth)
        val bookmarkWidth = 24.dp // 북마크 아이콘 너비 + 여백
        val availableTextWidth = maxWidthDp - bookmarkWidth - 32.dp // 패딩 고려

        GureumCard(
            modifier = Modifier
                .graphicsLayer {
                    val pageOffset =
                        pagerState.currentPage - page + pagerState.currentPageOffsetFraction
                    alpha = lerp(
                        start = 0.5f,
                        stop = 1f,
                        fraction = 1f - pageOffset.absoluteValue.coerceIn(0f, 1f),
                    )
                    lerp(
                        start = 1f,
                        stop = scaleSizeRatio,
                        fraction = pageOffset.absoluteValue.coerceIn(0f, 1f),
                    ).let {
                        scaleX = it
                        scaleY = it
                        val sign = if (pageOffset > 0) 1 else -1
                        translationX = sign * size.width * (1 - it) / 2
                    }
                },
            onClick = { onBookClick(book.userBookId) }
        ) {
            BookCardContent(
                book = book,
                availableTextWidth = availableTextWidth
            )
        }

        // 북마크 아이콘
        Icon(
            painter = painterResource(R.drawable.ic_bookmark),
            contentDescription = "북마크",
            tint = GureumTheme.colors.primary,
            modifier = Modifier
                .offset(x = maxWidthDp * 0.88f, y = 0.dp)
                .height(24.dp)
        )
    }
}

@Composable
private fun BookCardContent(
    book: UserBook,
    availableTextWidth: Dp
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // 책 표지
            BookCoverImage(
                imageUrl = book.imageUrl,
                modifier = Modifier
                    .height(80.dp)
                    .aspectRatio(0.75f)
                    .clip(RoundedCornerShape(4.dp)),
            )

            Spacer(Modifier.width(12.dp))

            // 책 정보
            BookInfo(
                book = book,
                modifier = Modifier
                    .weight(1f)
            )
        }

        Spacer(Modifier.height(16.dp))

        // 진행률 정보
        BookProgress(book = book)
    }
}

@Composable
private fun BookInfo(
    book: UserBook,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            // 제목 - 북마크 공간 고려해서 maxLines 조정
            Text(
                text = book.title,
                style = GureumTypography.titleMedium, // titleLarge -> titleMedium으로 줄임
                color = GureumTheme.colors.primary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth(0.85f) // 북마크 공간 확보
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = book.author,
                style = GureumTypography.bodySmall,
                color = GureumTheme.colors.gray500,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "누적 독서 시간: ${formatSecondsToReadableTime(book.totalReadTime)}",
                style = GureumTypography.bodySmall,
                color = GureumTheme.colors.gray500
            )
        }
    }
}

@Composable
private fun BookProgress(book: UserBook) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.End
    ) {
        val progress = if (book.totalPage > 0) {
            book.currentPage.toFloat() / book.totalPage
        } else 0f

        GureumLinearProgressBar(
            progress = progress,
            height = 6
        )

        Spacer(Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            book.startDate?.let { startDate ->
                Text(
                    text = "${formatDateToSimpleString(startDate)} ~",
                    style = GureumTypography.bodySmall,
                    color = GureumTheme.colors.gray500,
                )
            }

            Text(
                text = "${book.currentPage} / ${book.totalPage}",
                style = GureumTypography.labelSmall,
                color = GureumTheme.colors.gray500
            )
        }
    }
}
