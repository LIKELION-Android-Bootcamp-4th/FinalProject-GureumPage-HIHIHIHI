package com.hihihihi.gureumpage.ui.home.components

import android.content.res.Configuration
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.hihihihi.gureumpage.designsystem.components.GureumCard
import com.hihihihi.gureumpage.designsystem.components.TitleText
import com.hihihihi.gureumpage.designsystem.theme.GureumPageTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTypography
import kotlinx.coroutines.launch
import androidx.compose.foundation.interaction.MutableInteractionSource


@Composable
fun RandomQuoteSection() {
    Column(modifier = Modifier.padding(16.dp)) {
        TitleText("필사한 문장", isUnderline = true)
        Spacer(Modifier.height(12.dp))
        QuoteCard(
            quote = "“네가 4시에 온다면 난 3시부터 행복할거”",
            title = "어린왕자",
            date = "2025.07.29",
        )
    }
}


@Composable
fun QuoteCard(
    quote: String,
    title: String,
    date: String,
) {
    Box(
        modifier = Modifier
            .wrapContentHeight()
            .padding(bottom = 66.dp) // 구름 하단 여유 공간 확보
    ) {
        // 말풍선 본체
        GureumCard(modifier = Modifier.heightIn(min=100.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = title,
                        style = GureumTypography.titleLarge.copy(color = GureumTheme.colors.gray900)
                    )
                    Text(
                        text = date,
                        style = GureumTypography.bodySmall.copy(color = GureumTheme.colors.gray500),
                    )
                }

                Text(
                    text = quote,
                    style = GureumTypography.bodyMedium.copy(color = GureumTheme.colors.gray700),
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // 구름 캐릭터 - 말풍선 오른쪽 아래에 겹치도록 배치
        Box(
            modifier = Modifier
                .size(100.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 16.dp, y = 60.dp)
        ) {
            GuruemBulbLottie()
        }
    }
}


@Composable
fun GuruemBulbLottie(
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("cloud_bulb.json"))
    val scope = rememberCoroutineScope()
    val animatable = rememberLottieAnimatable()
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .clickable (
                interactionSource = interactionSource,
                indication = null
            ){
                scope.launch {
                    if (composition == null) return@launch

                    animatable.snapTo(progress = 0f) // 항상 처음부터 시작
                    animatable.animate(
                        composition = composition,
                        clipSpec = LottieClipSpec.Progress(0f, 0.9f),
                        iterations = 1
                    )
                    animatable.snapTo(progress = 0f) // 처음으로 되돌리기
                }
            }
    ) {
        if (composition != null) {
            LottieAnimation(
                composition = composition,
                progress = { animatable.progress },
                modifier = Modifier.fillMaxSize()
            )
        } else {
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
        RandomQuoteSection()

    }
}
