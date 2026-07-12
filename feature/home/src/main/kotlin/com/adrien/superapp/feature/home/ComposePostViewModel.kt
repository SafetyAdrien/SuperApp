package com.adrien.superapp.feature.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.adrien.superapp.core.common.AppResult
import com.adrien.superapp.core.domain.usecase.CreatePostUseCase
import com.adrien.superapp.core.navigation.AppRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ComposePostViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val createPostUseCase: CreatePostUseCase,
) : ViewModel() {

    private val route = savedStateHandle.toRoute<AppRoute.ComposePost>()

    private val _uiState = MutableStateFlow(ComposePostUiState(replyToPostId = route.replyToPostId))
    val uiState: StateFlow<ComposePostUiState> = _uiState.asStateFlow()

    fun onAction(action: ComposePostAction) {
        when (action) {
            is ComposePostAction.TextChanged -> _uiState.update { it.copy(text = action.text) }
            ComposePostAction.Submit -> submit()
        }
    }

    private fun submit() {
        val state = _uiState.value
        if (!state.canSubmit) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, error = null) }
            when (val result = createPostUseCase(text = state.text, replyToPostId = state.replyToPostId)) {
                is AppResult.Success -> _uiState.update { it.copy(isSubmitting = false, didSubmit = true) }
                is AppResult.Failure -> _uiState.update { it.copy(isSubmitting = false, error = result.error) }
            }
        }
    }
}
