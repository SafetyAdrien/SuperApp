package com.adrien.superapp.feature.home

import com.adrien.superapp.core.common.AppError

const val POST_MAX_LENGTH = 500

data class ComposePostUiState(
    val text: String = "",
    val replyToPostId: String? = null,
    val isSubmitting: Boolean = false,
    val error: AppError? = null,
    val didSubmit: Boolean = false,
) {
    val remainingCharacters: Int get() = POST_MAX_LENGTH - text.length
    val canSubmit: Boolean get() = text.isNotBlank() && remainingCharacters >= 0 && !isSubmitting
}

sealed interface ComposePostAction {
    data class TextChanged(val text: String) : ComposePostAction
    data object Submit : ComposePostAction
}
