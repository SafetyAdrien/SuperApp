package com.adrien.superapp.feature.settings

import com.adrien.superapp.core.common.ThemeMode

data class SettingsUiState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val useDynamicColor: Boolean = false,
    val isLoading: Boolean = true,
)

sealed interface SettingsAction {
    data class SetThemeMode(val themeMode: ThemeMode) : SettingsAction
    data class SetUseDynamicColor(val enabled: Boolean) : SettingsAction
}
