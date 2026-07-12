package com.adrien.superapp.core.domain.usecase

import com.adrien.superapp.core.common.AppError
import com.adrien.superapp.core.common.AppResult
import com.adrien.superapp.core.domain.repository.ProfileRepository
import com.adrien.superapp.core.domain.repository.ReactionRepository
import com.adrien.superapp.core.model.EntityType
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class ToggleReactionUseCase @Inject constructor(
    private val reactionRepository: ReactionRepository,
    private val profileRepository: ProfileRepository,
) {
    suspend operator fun invoke(
        entityType: EntityType,
        entityId: String,
        reaction: String = "like",
    ): AppResult<Unit> {
        val profile = profileRepository.observeCurrentProfile().firstOrNull()
            ?: return AppResult.Failure(AppError.NotFound)
        return reactionRepository.toggleReaction(entityType, entityId, profile.id, reaction)
    }
}
