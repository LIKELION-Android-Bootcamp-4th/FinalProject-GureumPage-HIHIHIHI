package com.hihihihi.gureumpage.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf

private val LightGureum = GureumColors.defaultLightColors()
private val DarkGureum = GureumColors.defaultDarkColors()

private val LightColorScheme = lightColorScheme(
    primary = LightGureum.primary,
    onPrimary = LightGureum.white,
    primaryContainer = LightGureum.primaryDeep,
    onPrimaryContainer = LightGureum.white,
    inversePrimary = LightGureum.primaryShallow,

    secondary = LightGureum.gray200,
    onSecondary = LightGureum.gray0,
//    secondaryContainer = LightGureum.dividerShallow,
//    onSecondaryContainer = LightGureum.gray100,

    tertiary = LightGureum.primary50,
    onTertiary = LightGureum.gray500,
//    tertiaryContainer = LightGureum.primary10,
//    onTertiaryContainer = LightGureum.gray500,

    error = LightGureum.systemRed,
    onError = LightGureum.white,
    errorContainer = LightGureum.systemRed,
    onErrorContainer = LightGureum.white,

    surface = LightGureum.card,
    onSurface = LightGureum.gray800,
//    surfaceContainerLowest = LightGureum.gray800,
    surfaceContainerLow = LightGureum.gray200,
    surfaceContainer = LightGureum.card,
//    surfaceContainerHigh = LightGureum.gray200,
    surfaceContainerHighest = LightGureum.card,

    background = LightGureum.background,
    onBackground = LightGureum.gray800,

    outline = LightGureum.gray200,
    outlineVariant = LightGureum.background50,
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkGureum.primary,
    onPrimary = DarkGureum.white,
    primaryContainer = DarkGureum.primaryDeep,
    onPrimaryContainer = DarkGureum.white,
    inversePrimary = DarkGureum.primaryShallow,

    secondary = DarkGureum.gray200,
    onSecondary = DarkGureum.gray0,

    tertiary = DarkGureum.primary50,
    onTertiary = DarkGureum.gray500,

    error = DarkGureum.systemRed,
    onError = DarkGureum.white,
    errorContainer = DarkGureum.systemRed,
    onErrorContainer = DarkGureum.white,

    surface = DarkGureum.card,
    onSurface = DarkGureum.gray800,
//    surfaceContainerLowest = DarkGureum.gray100,
    surfaceContainerLow = DarkGureum.gray200,
    surfaceContainer = DarkGureum.card,
//    surfaceContainerHigh = DarkGureum.gray600,
    surfaceContainerHighest = DarkGureum.card,

    background = DarkGureum.background,
    onBackground = DarkGureum.gray0,

    outline = DarkGureum.gray200,
    outlineVariant = DarkGureum.background50,
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