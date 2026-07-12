package com.adrien.superapp.core.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Semantic tokens Material 3's [ColorScheme] doesn't have a slot for
 * (success/warning/info, the text hierarchy, border/divider, elevated
 * surface). Read via [LocalSuperAppExtendedColors] / `SuperAppTheme.colors`.
 */
@Immutable
data class SuperAppExtendedColors(
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val border: Color,
    val divider: Color,
    val surfaceElevated: Color,
    val success: Color,
    val warning: Color,
    val error: Color,
    val info: Color,
)

internal val LightExtendedColors = SuperAppExtendedColors(
    textPrimary = LightTextPrimary,
    textSecondary = LightTextSecondary,
    textTertiary = LightTextTertiary,
    border = LightBorder,
    divider = LightDivider,
    surfaceElevated = LightSurfaceElevated,
    success = LightSuccess,
    warning = LightWarning,
    error = LightError,
    info = LightInfo,
)

internal val DarkExtendedColors = SuperAppExtendedColors(
    textPrimary = DarkTextPrimary,
    textSecondary = DarkTextSecondary,
    textTertiary = DarkTextTertiary,
    border = DarkBorder,
    divider = DarkDivider,
    surfaceElevated = DarkSurfaceElevated,
    success = DarkSuccess,
    warning = DarkWarning,
    error = DarkError,
    info = DarkInfo,
)

val LocalSuperAppExtendedColors = staticCompositionLocalOf { LightExtendedColors }

internal fun superAppLightColorScheme(): ColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = LightTextPrimary,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,
    outline = LightBorder,
    outlineVariant = LightDivider,
    error = LightError,
    onError = Color.White,
)

internal fun superAppDarkColorScheme(): ColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkTextPrimary,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkBorder,
    outlineVariant = DarkDivider,
    error = DarkError,
    onError = DarkOnPrimary,
)
