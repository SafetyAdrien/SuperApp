package com.adrien.superapp.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adrien.superapp.core.datastore.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = userPreferencesRepository.userPreferences
        .map { prefs ->
            SettingsUiState(
                themeMode = prefs.themeMode,
                useDynamicColor = prefs.useDynamicColor,
                isLoading = false,
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SettingsUiState(),
        )

    fun onAction(action: SettingsAction) {
        when (action) {
            is SettingsAction.SetThemeMode -> viewModelScope.launch {
                userPreferencesRepository.setThemeMode(action.themeMode)
            }
            is SettingsAction.SetUseDynamicColor -> viewModelScope.launch {
                userPreferencesRepository.setUseDynamicColor(action.enabled)
            }
        }
    }
}
