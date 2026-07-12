package com.adrien.superapp.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.adrien.superapp.core.domain.usecase.ObserveFeedUseCase
import com.adrien.superapp.core.domain.usecase.ToggleReactionUseCase
import com.adrien.superapp.core.model.EntityType
import com.adrien.superapp.core.model.PostWithAuthor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    observeFeedUseCase: ObserveFeedUseCase,
    private val toggleReactionUseCase: ToggleReactionUseCase,
) : ViewModel() {

    val feed: Flow<PagingData<PostWithAuthor>> = observeFeedUseCase().cachedIn(viewModelScope)

    fun onToggleReaction(postId: String) {
        viewModelScope.launch {
            toggleReactionUseCase(entityType = EntityType.POST, entityId = postId)
        }
    }
}
