package com.adrien.superapp.feature.editor

import com.adrien.superapp.core.model.Block

data class PageDetailUiState(
    val title: String = "",
    val blocks: List<Block> = emptyList(),
    val isLoading: Boolean = true,
    val notFound: Boolean = false,
)
