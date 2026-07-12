package com.adrien.superapp.feature.spaces

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adrien.superapp.core.domain.usecase.ObserveSpacesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SpacesViewModel @Inject constructor(
    observeSpacesUseCase: ObserveSpacesUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SpacesUiState())
    val uiState: StateFlow<SpacesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeSpacesUseCase().collect { spaces ->
                _uiState.update { it.copy(isLoading = false, spaces = spaces) }
            }
        }
    }
}
