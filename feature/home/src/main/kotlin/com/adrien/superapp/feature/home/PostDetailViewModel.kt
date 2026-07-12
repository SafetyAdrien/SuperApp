package com.adrien.superapp.feature.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.adrien.superapp.core.domain.usecase.ObservePostUseCase
import com.adrien.superapp.core.domain.usecase.ObserveRepliesUseCase
import com.adrien.superapp.core.domain.usecase.ToggleReactionUseCase
import com.adrien.superapp.core.model.EntityType
import com.adrien.superapp.core.navigation.AppRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    observePostUseCase: ObservePostUseCase,
    observeRepliesUseCase: ObserveRepliesUseCase,
    private val toggleReactionUseCase: ToggleReactionUseCase,
) : ViewModel() {

    private val route = savedStateHandle.toRoute<AppRoute.PostDetail>()
    val postId: String get() = route.postId

    val uiState: StateFlow<PostDetailUiState> = combine(
        observePostUseCase(route.postId),
        observeRepliesUseCase(route.postId),
    ) { post, replies ->
        PostDetailUiState(post = post, replies = replies, isLoading = false)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = PostDetailUiState(),
    )

    fun onToggleReaction(postId: String) {
        viewModelScope.launch {
            toggleReactionUseCase(entityType = EntityType.POST, entityId = postId)
        }
    }
}
