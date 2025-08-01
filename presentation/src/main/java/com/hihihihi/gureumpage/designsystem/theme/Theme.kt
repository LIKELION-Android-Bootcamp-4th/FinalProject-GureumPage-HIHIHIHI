package com.hihihihi.gureumpage.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = GureumColor.DarkPrimary,
    onPrimary = GureumColor.DarkGray100,
    primaryContainer = GureumColor.DarkPrimaryDeep,
    onPrimaryContainer = GureumColor.White,
    inversePrimary = GureumColor.DarkPrimaryShallow,

    secondary = GureumColor.DarkGray200,
    onSecondary = GureumColor.DarkGray0,

    tertiary = GureumColor.LightPrimary50,
    onTertiary = GureumColor.DarkGray500,

    error = GureumColor.SystemRed,
    onError = GureumColor.White,
    errorContainer = GureumColor.SystemRed,
    onErrorContainer = GureumColor.White,

    surface = GureumColor.DarkCard,
    onSurface = GureumColor.DarkGray100,
    surfaceContainerLowest = GureumColor.DarkGray100,
    surfaceContainerLow = GureumColor.DarkGray300,
    surfaceContainer = GureumColor.DarkCard,
    surfaceContainerHigh = GureumColor.DarkGray600,
    surfaceContainerHighest = GureumColor.DarkGray700,

    background = GureumColor.DarkBackground,
    onBackground = GureumColor.DarkGray0,

    outline = GureumColor.DarkGray200,
    outlineVariant = GureumColor.LightBackground50,
)

private val LightColorScheme = lightColorScheme(
    primary = GureumColor.LightPrimary,
    onPrimary = GureumColor.LightGray100,
    primaryContainer = GureumColor.LightPrimaryDeep,
    onPrimaryContainer = GureumColor.White,
    inversePrimary = GureumColor.LightPrimaryShallow,

    secondary = GureumColor.LightGray600,
    onSecondary = GureumColor.LightGray900,
    secondaryContainer = GureumColor.LightDividerShallow,
    onSecondaryContainer = GureumColor.LightGray100,

    tertiary = GureumColor.DarkPrimary50,
    onTertiary = GureumColor.LightGray300,
    tertiaryContainer = GureumColor.DarkPrimary10,
    onTertiaryContainer = GureumColor.LightGray300,

    error = GureumColor.SystemRed,
    onError = GureumColor.White,
    errorContainer = GureumColor.SystemRed,
    onErrorContainer = GureumColor.White,

    surface = GureumColor.LightCard,
    onSurface = GureumColor.LightGray100,
    surfaceContainerLowest = GureumColor.LightGray800,
    surfaceContainerLow = GureumColor.LightGray500,
    surfaceContainer = GureumColor.LightCard,
    surfaceContainerHigh = GureumColor.LightGray200,
    surfaceContainerHighest = GureumColor.LightGray150,

    background = GureumColor.LightBackground,
    onBackground = GureumColor.LightGray800,

    outline = GureumColor.LightGray600,
    outlineVariant = GureumColor.DarkBackground50,
)

@Composable
fun GureumPageTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = GureumTypography,
        content = content
    )
}