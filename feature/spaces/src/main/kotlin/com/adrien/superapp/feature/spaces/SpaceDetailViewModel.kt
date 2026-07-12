package com.adrien.superapp.feature.spaces

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.adrien.superapp.core.common.AppResult
import com.adrien.superapp.core.domain.usecase.CreatePageUseCase
import com.adrien.superapp.core.domain.usecase.ObservePagesUseCase
import com.adrien.superapp.core.domain.usecase.ObserveSpaceUseCase
import com.adrien.superapp.core.navigation.AppRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SpaceDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    observeSpaceUseCase: ObserveSpaceUseCase,
    observePagesUseCase: ObservePagesUseCase,
    private val createPageUseCase: CreatePageUseCase,
) : ViewModel() {

    private val route = savedStateHandle.toRoute<AppRoute.SpaceDetail>()

    private val isCreatingPage = MutableStateFlow(false)
    private val createdPageId = MutableStateFlow<String?>(null)

    val uiState: StateFlow<SpaceDetailUiState> = combine(
        observeSpaceUseCase(route.spaceId),
        observePagesUseCase(route.spaceId),
        isCreatingPage,
        createdPageId,
    ) { space, pages, creating, createdId ->
        SpaceDetailUiState(
            space = space,
            pages = pages,
            isLoading = false,
            isCreatingPage = creating,
            createdPageId = createdId,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SpaceDetailUiState(),
    )

    fun onCreatePageClick() {
        if (isCreatingPage.value) return
        viewModelScope.launch {
            isCreatingPage.value = true
            when (val result = createPageUseCase(spaceId = route.spaceId)) {
                is AppResult.Success -> createdPageId.value = result.data.id
                is AppResult.Failure -> Unit
            }
            isCreatingPage.value = false
        }
    }
}
