package com.adrien.superapp.core.common

import kotlin.math.abs

/** Coarse "il y a Xmin/h/j" formatting shared by any list showing timestamps. */
fun formatRelativeTime(epochMillis: Long, nowMillis: Long = System.currentTimeMillis()): String {
    val diffSeconds = (nowMillis - epochMillis) / 1000
    val absSeconds = abs(diffSeconds)
    return when {
        absSeconds < 60 -> "à l'instant"
        absSeconds < 3_600 -> "${absSeconds / 60} min"
        absSeconds < 86_400 -> "${absSeconds / 3_600} h"
        absSeconds < 604_800 -> "${absSeconds / 86_400} j"
        else -> "${absSeconds / 604_800} sem"
    }
}
