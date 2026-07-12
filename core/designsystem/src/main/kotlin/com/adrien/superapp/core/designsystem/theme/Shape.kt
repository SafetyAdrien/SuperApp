package com.adrien.superapp.core.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

object SuperAppRadius {
    val Small = RoundedCornerShape(8.dp)
    val Medium = RoundedCornerShape(12.dp)
    val Large = RoundedCornerShape(16.dp)
    val ExtraLarge = RoundedCornerShape(24.dp)
    val Pill = RoundedCornerShape(percent = 50)
}

val SuperAppShapes = Shapes(
    extraSmall = SuperAppRadius.Small,
    small = SuperAppRadius.Small,
    medium = SuperAppRadius.Medium,
    large = SuperAppRadius.Large,
    extraLarge = SuperAppRadius.ExtraLarge,
)
