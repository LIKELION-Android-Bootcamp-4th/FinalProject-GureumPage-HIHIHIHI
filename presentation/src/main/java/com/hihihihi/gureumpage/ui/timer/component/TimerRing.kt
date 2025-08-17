package com.hihihihi.gureumpage.ui.timer.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme.colors
import com.hihihihi.gureumpage.designsystem.theme.GureumTypography
import kotlin.math.min

@Composable
fun TimerRing(
    progress: Float,
    centerText: String,
    modifier: Modifier = Modifier,
    ringWidth: Dp = 20.dp,
    trackColor: Color? = null,
    progressColor: Color? = null,
    centerTextColor: Color? = null,
) {
    val theme = GureumTheme.colors
    val trackC = trackColor ?: theme.gray200
    val progressC = progressColor ?: theme.primary
    val textC = centerTextColor ?: theme.gray800

    val strokePx = with(LocalDensity.current) { ringWidth.toPx() }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val sizeMin = min(size.width, size.height)
            val diameter = sizeMin - strokePx
            val topLeft = Offset((size.width - diameter) / 2f, (size.height - diameter) / 2f)
            val arcSize = Size(diameter, diameter)

            // 트랙
            drawArc(
                color = trackC,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )
            // 진행
            drawArc(
                color = progressC,
                startAngle = -90f,
                sweepAngle = 360f * progress.coerceIn(0f, 1f),
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )
        }

        Text(
            text = centerText,
            style = GureumTypography.displayMedium,
            color = textC,
            textAlign = TextAlign.Center
        )
    }
}