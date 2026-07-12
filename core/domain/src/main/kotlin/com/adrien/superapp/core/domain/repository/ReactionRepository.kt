package com.adrien.superapp.core.domain.repository

import com.adrien.superapp.core.common.AppResult
import com.adrien.superapp.core.model.EntityType

interface ReactionRepository {
    /** Toggles [reaction] by [profileId] on the given entity; adds it if absent, removes it if present. */
    suspend fun toggleReaction(
        entityType: EntityType,
        entityId: String,
        profileId: String,
        reaction: String,
    ): AppResult<Unit>
}
