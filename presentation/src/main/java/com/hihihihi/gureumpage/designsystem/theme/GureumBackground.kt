package com.hihihihi.gureumpage.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class GureumBackground(
    val color: Color = Color.Unspecified,
    val tonalElevation: Dp = Dp.Unspecified,
) {
    companion object {
        fun defaultBackground(darkTheme: Boolean): GureumBackground {
            return if (darkTheme) {
                GureumBackground(
                    color = Color(0xFF212327),
                    tonalElevation = 0.dp,
                )
            } else {
                GureumBackground(
                    color = Color(0xFFFCFBF6),
                    tonalElevation = 0.dp,
                )
            }
        }
    }
}

val LocalBackgroundTheme: ProvidableCompositionLocal<GureumBackground> =
    staticCompositionLocalOf { GureumBackground() }
