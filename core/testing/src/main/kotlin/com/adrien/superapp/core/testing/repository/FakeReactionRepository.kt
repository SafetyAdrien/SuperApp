package com.adrien.superapp.core.testing.repository

import com.adrien.superapp.core.common.AppResult
import com.adrien.superapp.core.domain.repository.ReactionRepository
import com.adrien.superapp.core.model.EntityType

class FakeReactionRepository : ReactionRepository {

    private val reactions = mutableSetOf<String>()

    private fun key(entityType: EntityType, entityId: String, profileId: String, reaction: String) =
        "$entityType:$entityId:$profileId:$reaction"

    fun hasReacted(entityType: EntityType, entityId: String, profileId: String, reaction: String) =
        key(entityType, entityId, profileId, reaction) in reactions

    override suspend fun toggleReaction(
        entityType: EntityType,
        entityId: String,
        profileId: String,
        reaction: String,
    ): AppResult<Unit> {
        val key = key(entityType, entityId, profileId, reaction)
        if (!reactions.remove(key)) {
            reactions.add(key)
        }
        return AppResult.Success(Unit)
    }
}
