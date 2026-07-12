package com.adrien.superapp.feature.spaces

import com.adrien.superapp.core.common.AppError
import com.adrien.superapp.core.model.SpaceVisibility

data class CreateSpaceUiState(
    val name: String = "",
    val description: String = "",
    val visibility: SpaceVisibility = SpaceVisibility.PRIVATE,
    val isSubmitting: Boolean = false,
    val error: AppError? = null,
    val createdSpaceId: String? = null,
) {
    val canSubmit: Boolean get() = name.isNotBlank() && !isSubmitting
}

sealed interface CreateSpaceAction {
    data class NameChanged(val name: String) : CreateSpaceAction
    data class DescriptionChanged(val description: String) : CreateSpaceAction
    data class VisibilityChanged(val visibility: SpaceVisibility) : CreateSpaceAction
    data object Submit : CreateSpaceAction
}
