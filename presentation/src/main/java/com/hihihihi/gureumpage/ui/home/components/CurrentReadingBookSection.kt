package com.hihihihi.gureumpage.ui.home.components

import android.annotation.SuppressLint
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
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
import com.hihihihi.gureumpage.designsystem.components.GureumCard
import com.hihihihi.gureumpage.designsystem.components.GureumProgressBar
import com.hihihihi.gureumpage.designsystem.components.TitleText
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTypography
import kotlin.math.absoluteValue

@SuppressLint("RestrictedApi")
@Composable
fun CurrentReadingBookSection(
    books: List<UserBook>,
    onBookClick: (String) -> Unit
) {
    val pagerState = rememberPagerState(
        pageCount = { books.size }
    )
    val contentPadding = 30.dp
    val pageSpacing = 16.dp
    val scaleSizeRatio = 0.8f
    Surface(
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        color = GureumTheme.colors.background, // 원하는 배경색
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column {
            Box(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(top = 32.dp)
            ) {
                TitleText("독서중인 책", isUnderline = true)
            }

            Spacer(Modifier.height(10.dp))

            HorizontalPager(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally),
                state = pagerState,
                key = { books[it].userBookId },
                contentPadding = PaddingValues(horizontal = contentPadding),
                pageSpacing = pageSpacing,
            ) { page ->
                val book = books[page]
                BoxWithConstraints {
                    GureumCard(
                        Modifier
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
                        onClick = {
                            onBookClick(book.userBookId)
                        }
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(book.imageUrl)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "책 표지",
                                    modifier = Modifier
                                        .height(80.dp)
                                        .aspectRatio(0.75f)
                                        .clip(RoundedCornerShape(4.dp)),
                                    placeholder = painterResource(R.drawable.ic_cloud_reading),
                                    error = painterResource(R.drawable.ic_cloud_reading),
                                    contentScale = ContentScale.Crop
                                )

                                Spacer(Modifier.width(12.dp))

                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(84.dp), // 이미지 높이와 맞추기
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            book.title,
                                            style = GureumTypography.titleLarge.copy(
                                                color = GureumTheme.colors.primary,
                                            ),
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            book.author,
                                            style = GureumTypography.bodySmall.copy(
                                                color = GureumTheme.colors.gray500,
                                            ),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            "누적 독서 시간: ${formatSecondsToReadableTime(book.totalReadTime)}",
                                            style = GureumTypography.bodySmall.copy(
                                                color = GureumTheme.colors.gray500,
                                            )
                                        )
                                    }

                                    Text(
                                        "${formatDateToSimpleString(book.startDate!!)} ~",
                                        style = GureumTypography.bodySmall.copy(
                                            color = GureumTheme.colors.gray500,
                                        ),
                                        modifier = Modifier.align(Alignment.End)
                                    )
                                }
                            }

                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.End
                            ) {
                                Spacer(Modifier.height(4.dp))
                                GureumProgressBar(
                                    progress = book.currentPage.toFloat()/book.totalPage,
                                    height = 6
                                )
                                Spacer(Modifier.height(4.dp))
                                Text("${book.currentPage} / ${book.totalPage}",
                                    style = GureumTypography.labelSmall.copy(
                                        color = GureumTheme.colors.gray500,
                                    ))
                            }
                        }
                    }
                    val cardWidth = pxToDp(constraints.maxWidth) // box 사이즈 가져옴
                    val bookmarkOffsetX = cardWidth * 0.88f // box 너비의 88% 지점에 위치

                    Icon(
                        painter = painterResource(R.drawable.ic_bookmark),
                        contentDescription = "북마크",
                        tint = GureumTheme.colors.primary,
                        modifier = Modifier
                            .offset(x = bookmarkOffsetX, y = 0.dp)
                    )
                }
            }

            // 아래 그림자 끊기는 것 같아 추가
            Spacer(Modifier.height(10.dp))
        }
    }
}
