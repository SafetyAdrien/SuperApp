package com.adrien.superapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adrien.superapp.core.common.ThemeMode
import com.adrien.superapp.core.common.connectivity.ConnectivityObserver
import com.adrien.superapp.core.datastore.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class AppUiState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val useDynamicColor: Boolean = false,
    val isOnline: Boolean = true,
    val isLoading: Boolean = true,
)

@HiltViewModel
class AppViewModel @Inject constructor(
    userPreferencesRepository: UserPreferencesRepository,
    connectivityObserver: ConnectivityObserver,
) : ViewModel() {

    val uiState: StateFlow<AppUiState> = combine(
        userPreferencesRepository.userPreferences,
        connectivityObserver.isOnline,
    ) { preferences, isOnline ->
        AppUiState(
            themeMode = preferences.themeMode,
            useDynamicColor = preferences.useDynamicColor,
            isOnline = isOnline,
            isLoading = false,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AppUiState(),
    )
}
