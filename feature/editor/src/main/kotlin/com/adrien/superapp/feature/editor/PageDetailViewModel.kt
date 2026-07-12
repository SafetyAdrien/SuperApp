package com.adrien.superapp.feature.editor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.adrien.superapp.core.domain.usecase.ObservePageUseCase
import com.adrien.superapp.core.domain.usecase.RenamePageUseCase
import com.adrien.superapp.core.navigation.AppRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PageDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    observePageUseCase: ObservePageUseCase,
    private val renamePageUseCase: RenamePageUseCase,
) : ViewModel() {

    private val route = savedStateHandle.toRoute<AppRoute.PageDetail>()

    private val _uiState = MutableStateFlow(PageDetailUiState())
    val uiState: StateFlow<PageDetailUiState> = _uiState.asStateFlow()

    /** Only the first load seeds [PageDetailUiState.title] — later emissions are our own writes echoing back. */
    private var titleInitialized = false

    init {
        viewModelScope.launch {
            observePageUseCase(route.pageId).collect { page ->
                when {
                    page == null -> _uiState.update { it.copy(isLoading = false, notFound = true) }
                    !titleInitialized -> {
                        titleInitialized = true
                        _uiState.update { it.copy(isLoading = false, title = page.title) }
                    }
                    else -> Unit
                }
            }
        }
    }

    fun onTitleChanged(title: String) {
        _uiState.update { it.copy(title = title) }
        viewModelScope.launch {
            renamePageUseCase(route.pageId, title)
        }
    }
}
