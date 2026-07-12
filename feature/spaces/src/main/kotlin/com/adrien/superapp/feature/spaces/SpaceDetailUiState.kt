package com.adrien.superapp.feature.spaces

import com.adrien.superapp.core.model.Page
import com.adrien.superapp.core.model.SpaceWithStats

data class SpaceDetailUiState(
    val space: SpaceWithStats? = null,
    val pages: List<Page> = emptyList(),
    val isLoading: Boolean = true,
    val isCreatingPage: Boolean = false,
    val createdPageId: String? = null,
)
