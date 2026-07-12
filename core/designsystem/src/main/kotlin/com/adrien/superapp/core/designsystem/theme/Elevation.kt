package com.adrien.superapp.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Elevation stays subtle by design (see docs/DESIGN_SYSTEM.md) — most surfaces
 * use zero shadow and separate via color/spacing/border instead. Real
 * elevation is reserved for floating elements (FAB, sheets, dropdowns).
 */
@Immutable
data class SuperAppElevation(
    val none: Dp = 0.dp,
    val surface: Dp = 1.dp,
    val floating: Dp = 6.dp,
    val modal: Dp = 12.dp,
)

val LocalSuperAppElevation = staticCompositionLocalOf { SuperAppElevation() }
