package com.adrien.superapp.feature.spaces

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.adrien.superapp.core.common.AppResult
import com.adrien.superapp.core.domain.usecase.CreateCollectionUseCase
import com.adrien.superapp.core.domain.usecase.CreatePageUseCase
import com.adrien.superapp.core.domain.usecase.ObserveCollectionsUseCase
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
    observeCollectionsUseCase: ObserveCollectionsUseCase,
    private val createPageUseCase: CreatePageUseCase,
    private val createCollectionUseCase: CreateCollectionUseCase,
) : ViewModel() {

    private val route = savedStateHandle.toRoute<AppRoute.SpaceDetail>()

    private val isCreatingPage = MutableStateFlow(false)
    private val createdPageId = MutableStateFlow<String?>(null)
    private val isCreatingCollection = MutableStateFlow(false)
    private val createdCollectionId = MutableStateFlow<String?>(null)

    private val pageCreationState = combine(isCreatingPage, createdPageId) { creating, id -> creating to id }
    private val collectionCreationState = combine(isCreatingCollection, createdCollectionId) { creating, id -> creating to id }

    val uiState: StateFlow<SpaceDetailUiState> = combine(
        observeSpaceUseCase(route.spaceId),
        observePagesUseCase(route.spaceId),
        observeCollectionsUseCase(route.spaceId),
        pageCreationState,
        collectionCreationState,
    ) { space, pages, collections, pageCreation, collectionCreation ->
        SpaceDetailUiState(
            space = space,
            pages = pages,
            collections = collections,
            isLoading = false,
            isCreatingPage = pageCreation.first,
            createdPageId = pageCreation.second,
            isCreatingCollection = collectionCreation.first,
            createdCollectionId = collectionCreation.second,
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

    fun onCreateCollectionClick() {
        if (isCreatingCollection.value) return
        viewModelScope.launch {
            isCreatingCollection.value = true
            when (val result = createCollectionUseCase(spaceId = route.spaceId)) {
                is AppResult.Success -> createdCollectionId.value = result.data.id
                is AppResult.Failure -> Unit
            }
            isCreatingCollection.value = false
        }
    }
}
