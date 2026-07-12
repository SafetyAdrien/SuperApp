package com.adrien.superapp.feature.editor

data class PageDetailUiState(
    val title: String = "",
    val isLoading: Boolean = true,
    val notFound: Boolean = false,
)
