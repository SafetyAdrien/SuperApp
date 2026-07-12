package com.adrien.superapp.core.datastore

import com.adrien.superapp.core.common.DataEnvironment
import com.adrien.superapp.core.common.ThemeMode

data class UserPreferences(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val useDynamicColor: Boolean = false,
    val onboardingCompleted: Boolean = false,
    val selectedHomeTabRoute: String? = null,
    val dataEnvironment: DataEnvironment = DataEnvironment.LOCAL_DEMO,
    val syncOnWifiOnly: Boolean = false,
    val reduceMotion: Boolean = false,
)
