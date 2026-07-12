package com.adrien.superapp.feature.spaces

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adrien.superapp.core.common.AppResult
import com.adrien.superapp.core.domain.usecase.CreateSpaceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateSpaceViewModel @Inject constructor(
    private val createSpaceUseCase: CreateSpaceUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateSpaceUiState())
    val uiState: StateFlow<CreateSpaceUiState> = _uiState.asStateFlow()

    fun onAction(action: CreateSpaceAction) {
        when (action) {
            is CreateSpaceAction.NameChanged -> _uiState.update { it.copy(name = action.name) }
            is CreateSpaceAction.DescriptionChanged -> _uiState.update { it.copy(description = action.description) }
            is CreateSpaceAction.VisibilityChanged -> _uiState.update { it.copy(visibility = action.visibility) }
            CreateSpaceAction.Submit -> submit()
        }
    }

    private fun submit() {
        val state = _uiState.value
        if (!state.canSubmit) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, error = null) }
            when (
                val result = createSpaceUseCase(
                    name = state.name,
                    description = state.description,
                    visibility = state.visibility,
                )
            ) {
                is AppResult.Success -> _uiState.update { it.copy(isSubmitting = false, createdSpaceId = result.data.id) }
                is AppResult.Failure -> _uiState.update { it.copy(isSubmitting = false, error = result.error) }
            }
        }
    }
}
