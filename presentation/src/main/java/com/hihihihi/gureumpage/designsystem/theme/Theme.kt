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
    onBackground = DarkGureum.gray800,

    outline = DarkGureum.gray200,
    outlineVariant = DarkGureum.background50,
)

private val LocalDarkTheme = compositionLocalOf { true }
private val LocalColors = compositionLocalOf<GureumColors> {
    error("색상이 제공되지 않습니다. GureumTheme Provider 래핑을 확인해주세요")
}

@Composable
fun GureumPageTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    colors: GureumColors = if (darkTheme) GureumColors.defaultDarkColors() else GureumColors.defaultLightColors(),
    background: GureumBackground = GureumBackground.defaultBackground(darkTheme),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(
        LocalDarkTheme provides darkTheme,
        LocalColors provides colors,
        LocalBackgroundTheme provides background,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = GureumTypography,
            content = content
        )
    }
}

object GureumTheme {
    val isDarkTheme: Boolean
        @Composable
        @ReadOnlyComposable
        get() = LocalDarkTheme.current

    val colors: GureumColors
        @Composable
        @ReadOnlyComposable
        get() = LocalColors.current

    val background: GureumBackground
        @Composable
        @ReadOnlyComposable
        get() = LocalBackgroundTheme.current
}