package com.adrien.superapp.feature.spaces

import com.adrien.superapp.core.model.SpaceWithStats

data class SpacesUiState(
    val isLoading: Boolean = true,
    val spaces: List<SpaceWithStats> = emptyList(),
)
