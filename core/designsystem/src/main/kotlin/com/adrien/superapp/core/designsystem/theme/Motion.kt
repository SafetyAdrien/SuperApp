package com.adrien.superapp.core.designsystem.theme

import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext

@Immutable
data class SuperAppMotion(
    val fastMs: Int = 120,
    val standardMs: Int = 180,
    val emphasizedMs: Int = 240,
    /** True when the system "remove animations" accessibility setting is on. */
    val reduceMotion: Boolean = false,
) {
    /** Duration to actually use for a "standard" transition, respecting reduce-motion. */
    fun standard(): Int = if (reduceMotion) 0 else standardMs
    fun fast(): Int = if (reduceMotion) 0 else fastMs
    fun emphasized(): Int = if (reduceMotion) 0 else emphasizedMs
}

val LocalSuperAppMotion = staticCompositionLocalOf { SuperAppMotion() }

/** Reads `Settings.Global.ANIMATOR_DURATION_SCALE`; 0 means the user disabled animations. */
@Composable
fun rememberSystemReducedMotion(): Boolean {
    val context = LocalContext.current
    return remember(context) {
        val scale = Settings.Global.getFloat(
            context.contentResolver,
            Settings.Global.ANIMATOR_DURATION_SCALE,
            1f,
        )
        scale == 0f
    }
}
