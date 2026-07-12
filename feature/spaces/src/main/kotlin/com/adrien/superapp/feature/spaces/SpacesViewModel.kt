package com.adrien.superapp.feature.spaces

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adrien.superapp.core.domain.usecase.ObserveRecentPagesUseCase
import com.adrien.superapp.core.domain.usecase.ObserveSpacesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SpacesViewModel @Inject constructor(
    observeSpacesUseCase: ObserveSpacesUseCase,
    observeRecentPagesUseCase: ObserveRecentPagesUseCase,
) : ViewModel() {

    val uiState: StateFlow<SpacesUiState> = combine(
        observeSpacesUseCase(),
        observeRecentPagesUseCase(),
    ) { spaces, recentPages ->
        SpacesUiState(isLoading = false, recentPages = recentPages, spaces = spaces)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SpacesUiState(),
    )
}
