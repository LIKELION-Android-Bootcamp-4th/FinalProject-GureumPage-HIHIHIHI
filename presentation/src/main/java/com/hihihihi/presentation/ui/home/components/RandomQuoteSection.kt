package com.hihihihi.presentation.ui.home.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieClipSpec
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieAnimatable
import com.airbnb.lottie.compose.rememberLottieComposition
import com.hihihihi.presentation.designsystem.components.GureumCard
import com.hihihihi.presentation.designsystem.components.Semi16Text
import com.hihihihi.presentation.designsystem.theme.GureumPageTheme
import com.hihihihi.presentation.designsystem.theme.GureumTheme
import com.hihihihi.presentation.designsystem.theme.GureumTypography
import com.hihihihi.presentation.ui.home.mock.dummyQuotes
import com.hihihihi.presentation.ui.model.QuoteUiModel
import com.hihihihi.presentation.utils.formatDateToSimpleString
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@Composable
fun RandomQuoteSection(
    quotes: List<QuoteUiModel>,
) {
    // 현재 인덱스 저장해서 다음 랜덤때 제외하도록
    var currentIndex by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .background(GureumTheme.colors.background)
            .padding(16.dp),
    ) {
        Semi16Text("필사한 문장", isUnderline = true)
        Spacer(Modifier.height(12.dp))
        if (quotes.isNotEmpty()) {
            // 나중에 수정이나 삭제했을때 사이즈보다 커지거나 하면 문제가 생길 수도 있어 나머지 로직 넣었습니다
            val quote = quotes[currentIndex % quotes.size]
            QuoteCard(
                quote = quote.content,
                title = quote.title,
                date = formatDateToSimpleString(quote.createdAt),
                onClick = {
                    if (quotes.size > 1) {  // 1일때는 안바뀌도록
                        currentIndex = (quotes.indices - currentIndex).random()
                    }
                },
            )
        } else {
            // 필사 목록 비어있을 때 처리
            QuoteCard(
                quote = "아직 필사가 등록되지 않았어요. 필사를 하나 추가하고 구름이를 눌러보세요! 작성한 필사 중 하나를 랜덤으로 소개해드릴게요.",
                title = "구름한장",
                date = formatDateToSimpleString(LocalDateTime.now()),
                onClick = {
                },
            )
        }
    }
}

@Composable
fun QuoteCard(
    quote: String,
    title: String,
    date: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .wrapContentHeight()
            .padding(bottom = 30.dp), // 구름 하단 여유 공간 확보
        //TODO 더 나은 방식이 있지 않을까
    ) {
        // 말풍선 본체
        GureumCard(modifier = Modifier.heightIn(min = 100.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // 책 제목
                    Text(
                        text = title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = GureumTypography.titleLarge.copy(color = GureumTheme.colors.gray900),
                        modifier = Modifier.weight(1f), // 날짜 공간 보장용
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    // 날짜
                    Text(
                        text = date,
                        style = GureumTypography.bodySmall.copy(color = GureumTheme.colors.gray500),
                    )
                }
                // 필사
                Text(
                    text = "\"${quote}\"",
                    style = GureumTypography.bodyMedium.copy(color = GureumTheme.colors.gray700),
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        // 구름 캐릭터
        Box(
            modifier = Modifier
                .size(100.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 16.dp, y = 60.dp),
        ) {
            GuruemBulbLottie(onClick)
        }
    }
}


@Composable
fun GuruemBulbLottie(
    onClick: () -> Unit,
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("cloud_bulb.json"))
    val scope = rememberCoroutineScope()
    val animatable = rememberLottieAnimatable()
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
            ) {
                onClick()
                scope.launch {
                    if (composition == null) return@launch

                    animatable.snapTo(progress = 0f) // 항상 처음부터 시작
                    // 재생
                    animatable.animate(
                        composition = composition,
                        clipSpec = LottieClipSpec.Progress(0f, 0.9f),
                        iterations = 1,
                    )
                    animatable.snapTo(progress = 0f) // 처음으로 되돌리기
                }
            },
    ) {
        if (composition != null) {
            LottieAnimation(
                composition = composition,
                progress = { animatable.progress },
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            // TODO 로티가 로딩이 좀 느려서 자리가 비어있다가 생기길래 일단 인디케이터를 넣어놨는데 어떻게 하면 좋을지🥲
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}


@Preview(name = "DarkMode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "LightMode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun SpeechBubbleCardPreview() {
    GureumPageTheme {
        RandomQuoteSection(dummyQuotes)
    }
}
