package com.hihihihi.gureumpage.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = GureumColor.LightPrimary,
    onPrimary = GureumColor.Gray100,
    primaryContainer = GureumColor.LightPrimaryDeep,
    onPrimaryContainer = GureumColor.White,
    inversePrimary = GureumColor.LightPrimaryShallow,

    secondary = GureumColor.Gray600,
    onSecondary = GureumColor.Gray900,
    secondaryContainer = GureumColor.LightDividerShallow,
    onSecondaryContainer = GureumColor.Gray100,

    tertiary = GureumColor.LightPrimary50,
    onTertiary = GureumColor.Gray300,
    tertiaryContainer = GureumColor.LightPrimary10,
    onTertiaryContainer = GureumColor.Gray300,

    error = GureumColor.SystemRed,
    onError = GureumColor.White,
    errorContainer = GureumColor.SystemRed,
    onErrorContainer = GureumColor.White,

    surface = GureumColor.LightCard,
    onSurface = GureumColor.Gray100,
    surfaceContainerLowest = GureumColor.Gray800,
    surfaceContainerLow = GureumColor.Gray500,
    surfaceContainer = GureumColor.Gray400,
    surfaceContainerHigh = GureumColor.Gray200,
    surfaceContainerHighest = GureumColor.Gray150,

    background = GureumColor.LightBackground,
    onBackground = GureumColor.Gray0,

    outline = GureumColor.Gray600,
    outlineVariant = GureumColor.LightBackground50,
)

private val LightColorScheme = lightColorScheme(
    primary = GureumColor.LightPrimary,
    onPrimary = GureumColor.Gray800,
    primaryContainer = GureumColor.LightPrimaryDeep,
    onPrimaryContainer = GureumColor.White,
    inversePrimary = GureumColor.LightPrimaryShallow,

    secondary = GureumColor.Gray200,
    onSecondary = GureumColor.Gray0,
    secondaryContainer = GureumColor.LightDividerShallow,
    onSecondaryContainer = GureumColor.Gray800,

    tertiary = GureumColor.LightPrimary50,
    onTertiary = GureumColor.Gray500,
    tertiaryContainer = GureumColor.LightPrimary10,
    onTertiaryContainer = GureumColor.Gray500,

    error = GureumColor.SystemRed,
    onError = GureumColor.White,
    errorContainer = GureumColor.SystemRed,
    onErrorContainer = GureumColor.White,

    surface = GureumColor.LightCard,
    onSurface = GureumColor.Gray800,
    surfaceContainerLowest = GureumColor.Gray100,
    surfaceContainerLow = GureumColor.Gray300,
    surfaceContainer = GureumColor.Gray400,
    surfaceContainerHigh = GureumColor.Gray600,
    surfaceContainerHighest = GureumColor.Gray700,

    background = GureumColor.LightBackground,
    onBackground = GureumColor.Gray900,

    outline = GureumColor.Gray200,
    outlineVariant = GureumColor.LightBackground50,
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