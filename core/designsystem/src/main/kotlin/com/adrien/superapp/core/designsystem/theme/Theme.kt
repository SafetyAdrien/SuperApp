package com.adrien.superapp.core.designsystem.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import com.adrien.superapp.core.common.ThemeMode

/**
 * App-wide theme. Material 3 is the technical foundation; [SuperAppExtendedColors],
 * [SuperAppSpacing], [SuperAppElevation], and [SuperAppMotion] are the actual
 * design-token source of truth screens should read from — never a hardcoded
 * hex color or raw dp literal in feature code.
 */
@Composable
fun SuperAppTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    useDynamicColor: Boolean = false,
    reduceMotion: Boolean = false,
    content: @Composable () -> Unit,
) {
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    val context = LocalContext.current
    // minSdk is 31 (Android 12), so dynamic color (Build.VERSION_CODES.S+) is
    // always available on this app's supported OS range.
    val colorScheme = when {
        useDynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        darkTheme -> superAppDarkColorScheme()
        else -> superAppLightColorScheme()
    }

    val extendedColors = if (darkTheme) DarkExtendedColors else LightExtendedColors
    val motion = SuperAppMotion(reduceMotion = reduceMotion)

    CompositionLocalProvider(
        LocalSuperAppExtendedColors provides extendedColors,
        LocalSuperAppSpacing provides SuperAppSpacing(),
        LocalSuperAppElevation provides SuperAppElevation(),
        LocalSuperAppMotion provides motion,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = SuperAppTypography,
            shapes = SuperAppShapes,
            content = content,
        )
    }
}

/** Convenience accessor mirroring `MaterialTheme.colorScheme` etc. */
object SuperAppTheme {
    val extendedColors: SuperAppExtendedColors
        @Composable
        get() = LocalSuperAppExtendedColors.current

    val spacing: SuperAppSpacing
        @Composable
        get() = LocalSuperAppSpacing.current

    val elevation: SuperAppElevation
        @Composable
        get() = LocalSuperAppElevation.current

    val motion: SuperAppMotion
        @Composable
        get() = LocalSuperAppMotion.current
}
