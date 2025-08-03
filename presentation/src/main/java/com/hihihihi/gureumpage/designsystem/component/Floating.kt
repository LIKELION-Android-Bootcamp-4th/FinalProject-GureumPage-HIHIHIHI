package com.hihihihi.gureumpage.designsystem.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Floating(
    modifier: Modifier = Modifier,
    floatingOffset: Dp = 10.dp,
    durationMillis: Int = 2000,
    content: @Composable () -> Unit
) {
    val offsetY by rememberInfiniteTransition(label = "FloatingTransition").animateFloat(
        initialValue = 0f,
        targetValue = floatingOffset.value,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "OffsetYAnimation"
    )

    Box(modifier = modifier.offset(y = offsetY.dp)) {
        content()
    }
}