package com.adrien.superapp.feature.home

import com.adrien.superapp.core.model.PostWithAuthor

data class PostDetailUiState(
    val post: PostWithAuthor? = null,
    val replies: List<PostWithAuthor> = emptyList(),
    val isLoading: Boolean = true,
)
