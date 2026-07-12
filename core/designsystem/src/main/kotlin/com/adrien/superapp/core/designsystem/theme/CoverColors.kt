package com.adrien.superapp.core.designsystem.theme

import androidx.compose.ui.graphics.Color

/**
 * Named solid-color page covers (Notion-like default covers, before a real photo cover exists).
 * A page stores the key (`Page.coverColorKey`), never the hex — screens look it up here so no
 * hardcoded color ever appears outside the design-system layer.
 */
val SuperAppCoverColors: Map<String, Color> = linkedMapOf(
    "amber" to Color(0xFFFCEBD0),
    "rose" to Color(0xFFFBE0E0),
    "mint" to Color(0xFFDFF3E8),
    "sky" to Color(0xFFDFEBFB),
    "lavender" to Color(0xFFE7E0F8),
    "stone" to Color(0xFFEDEBE7),
)
