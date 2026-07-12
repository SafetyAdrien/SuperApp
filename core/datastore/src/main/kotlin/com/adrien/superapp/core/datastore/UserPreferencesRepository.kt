package com.adrien.superapp.core.datastore

import com.adrien.superapp.core.common.DataEnvironment
import com.adrien.superapp.core.common.ThemeMode
import kotlinx.coroutines.flow.Flow

/**
 * Single source of truth for user-scoped app preferences (theme, dynamic color,
 * onboarding state, sync settings, ...). Backed by Preferences DataStore, never
 * `SharedPreferences`.
 */
interface UserPreferencesRepository {
    val userPreferences: Flow<UserPreferences>

    suspend fun setThemeMode(themeMode: ThemeMode)
    suspend fun setUseDynamicColor(useDynamicColor: Boolean)
    suspend fun setOnboardingCompleted(completed: Boolean)
    suspend fun setSelectedHomeTabRoute(route: String)
    suspend fun setDataEnvironment(dataEnvironment: DataEnvironment)
    suspend fun setSyncOnWifiOnly(wifiOnly: Boolean)
    suspend fun setReduceMotion(reduceMotion: Boolean)
}
