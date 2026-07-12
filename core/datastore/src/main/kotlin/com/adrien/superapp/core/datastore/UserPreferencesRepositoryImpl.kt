package com.adrien.superapp.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.adrien.superapp.core.common.DataEnvironment
import com.adrien.superapp.core.common.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private object Keys {
    val THEME_MODE = stringPreferencesKey("theme_mode")
    val USE_DYNAMIC_COLOR = booleanPreferencesKey("use_dynamic_color")
    val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
    val SELECTED_HOME_TAB_ROUTE = stringPreferencesKey("selected_home_tab_route")
    val DATA_ENVIRONMENT = stringPreferencesKey("data_environment")
    val SYNC_ON_WIFI_ONLY = booleanPreferencesKey("sync_on_wifi_only")
    val REDUCE_MOTION = booleanPreferencesKey("reduce_motion")
}

@Singleton
class UserPreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : UserPreferencesRepository {

    override val userPreferences: Flow<UserPreferences> = dataStore.data.map { prefs ->
        UserPreferences(
            themeMode = prefs[Keys.THEME_MODE]?.let(ThemeMode::valueOf) ?: ThemeMode.SYSTEM,
            useDynamicColor = prefs[Keys.USE_DYNAMIC_COLOR] ?: false,
            onboardingCompleted = prefs[Keys.ONBOARDING_COMPLETED] ?: false,
            selectedHomeTabRoute = prefs[Keys.SELECTED_HOME_TAB_ROUTE],
            dataEnvironment = prefs[Keys.DATA_ENVIRONMENT]?.let(DataEnvironment::valueOf)
                ?: DataEnvironment.LOCAL_DEMO,
            syncOnWifiOnly = prefs[Keys.SYNC_ON_WIFI_ONLY] ?: false,
            reduceMotion = prefs[Keys.REDUCE_MOTION] ?: false,
        )
    }

    override suspend fun setThemeMode(themeMode: ThemeMode) {
        dataStore.edit { it[Keys.THEME_MODE] = themeMode.name }
    }

    override suspend fun setUseDynamicColor(useDynamicColor: Boolean) {
        dataStore.edit { it[Keys.USE_DYNAMIC_COLOR] = useDynamicColor }
    }

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        dataStore.edit { it[Keys.ONBOARDING_COMPLETED] = completed }
    }

    override suspend fun setSelectedHomeTabRoute(route: String) {
        dataStore.edit { it[Keys.SELECTED_HOME_TAB_ROUTE] = route }
    }

    override suspend fun setDataEnvironment(dataEnvironment: DataEnvironment) {
        dataStore.edit { it[Keys.DATA_ENVIRONMENT] = dataEnvironment.name }
    }

    override suspend fun setSyncOnWifiOnly(wifiOnly: Boolean) {
        dataStore.edit { it[Keys.SYNC_ON_WIFI_ONLY] = wifiOnly }
    }

    override suspend fun setReduceMotion(reduceMotion: Boolean) {
        dataStore.edit { it[Keys.REDUCE_MOTION] = reduceMotion }
    }
}
