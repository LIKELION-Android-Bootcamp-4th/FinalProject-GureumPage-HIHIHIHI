package com.hihihihi.gureumpage.ui.timer.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.sp
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTypography

@Composable
fun CountdownOverlayWithHole(
    number: Int,
    holeRect: Rect?,
    holeCornerRadiusDp: Float = 16f,
    modifier: Modifier = Modifier
) {
    val colors = GureumTheme.colors
    val density = LocalDensity.current

    // 라이트/다크 분기
    val isDark = runCatching { GureumTheme.isDarkTheme }.getOrElse { isSystemInDarkTheme() }

    // 테마에 맞춰 대비되는 글자색 선택
    val textColor = if (isDark) {
        // 다크모드 → 밝은 글자
        runCatching { GureumTheme.colors.white }.getOrElse { Color.White }
    } else {
        // 라이트모드 → 진한 글자
        runCatching { GureumTheme.colors.gray900 }.getOrElse { Color.Black }
    }

    val animSpec = tween<Float>(durationMillis = 220)
    val alpha = animateFloatAsState(targetValue = 1f, animationSpec = animSpec, label = "cnt-alpha").value
    val scale = animateFloatAsState(targetValue = 1f, animationSpec = animSpec, label = "cnt-scale").value

    val cornerPx = with(density) { holeCornerRadiusDp.dp.toPx() }


    Box(
        modifier = modifier
            .fillMaxSize()
            // BlendMode.Clear가 정상 동작하도록 오프스크린 컴포지팅
            .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
            .drawWithContent {

                drawRect(color = colors.background, size = size, style = Fill)

                holeRect?.let { r ->
                    drawRoundRect(
                        color = Color.Transparent,
                        topLeft = r.topLeft,
                        size = r.size,
                        cornerRadius = CornerRadius(cornerPx, cornerPx),
                        blendMode = BlendMode.Clear
                    )
                }
                drawContent()
            }
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) { awaitPointerEvent() }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        // 구멍을 뚫은 '뒤 배경' 위에 숫자를 얹는다
        Text(
            text = number.toString(),
            style = GureumTypography.displayLarge.copy(
                fontSize = 120.sp,
                shadow = Shadow(                      // 가독성 살짝 보강(선택)
                    color = Color.Black.copy(alpha = 0.25f),
                    offset = Offset(0f, 2f),
                    blurRadius = 6f
                )
            ),
            color = textColor,
            modifier = Modifier.graphicsLayer {
                this.alpha = alpha
                this.scaleX = scale
                this.scaleY = scale
            }
        )
    }

}