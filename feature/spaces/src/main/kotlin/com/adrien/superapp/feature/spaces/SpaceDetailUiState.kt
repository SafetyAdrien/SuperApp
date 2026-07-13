package com.adrien.superapp.feature.spaces

import com.adrien.superapp.core.model.Collection
import com.adrien.superapp.core.model.Page
import com.adrien.superapp.core.model.SpaceWithStats

data class SpaceDetailUiState(
    val space: SpaceWithStats? = null,
    val pages: List<Page> = emptyList(),
    val collections: List<Collection> = emptyList(),
    val isLoading: Boolean = true,
    val isCreatingPage: Boolean = false,
    val createdPageId: String? = null,
    val isCreatingCollection: Boolean = false,
    val createdCollectionId: String? = null,
)
