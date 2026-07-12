package com.adrien.superapp.core.domain.usecase

import com.adrien.superapp.core.common.AppError
import com.adrien.superapp.core.common.AppResult
import com.adrien.superapp.core.domain.repository.ProfileRepository
import com.adrien.superapp.core.domain.repository.SpaceRepository
import com.adrien.superapp.core.model.Space
import com.adrien.superapp.core.model.SpaceVisibility
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class CreateSpaceUseCase @Inject constructor(
    private val spaceRepository: SpaceRepository,
    private val profileRepository: ProfileRepository,
) {
    suspend operator fun invoke(
        name: String,
        description: String? = null,
        visibility: SpaceVisibility = SpaceVisibility.PRIVATE,
    ): AppResult<Space> {
        val trimmedName = name.trim()
        if (trimmedName.isEmpty()) {
            return AppResult.Failure(AppError.Unknown())
        }
        val owner = profileRepository.observeCurrentProfile().firstOrNull()
            ?: return AppResult.Failure(AppError.NotFound)

        return spaceRepository.createSpace(
            ownerId = owner.id,
            name = trimmedName,
            description = description?.trim()?.takeIf { it.isNotEmpty() },
            visibility = visibility,
        )
    }
}
