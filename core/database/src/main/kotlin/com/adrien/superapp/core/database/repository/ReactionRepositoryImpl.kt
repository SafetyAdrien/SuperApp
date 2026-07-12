package com.adrien.superapp.core.database.repository

import com.adrien.superapp.core.common.AppError
import com.adrien.superapp.core.common.AppResult
import com.adrien.superapp.core.database.dao.ReactionDao
import com.adrien.superapp.core.database.entity.ReactionEntity
import com.adrien.superapp.core.domain.repository.ReactionRepository
import com.adrien.superapp.core.model.EntityType
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReactionRepositoryImpl @Inject constructor(
    private val reactionDao: ReactionDao,
) : ReactionRepository {

    override suspend fun toggleReaction(
        entityType: EntityType,
        entityId: String,
        profileId: String,
        reaction: String,
    ): AppResult<Unit> = try {
        val existingId = reactionDao.findId(entityType.name, entityId, profileId, reaction)
        if (existingId != null) {
            reactionDao.deleteById(existingId)
        } else {
            reactionDao.insert(
                ReactionEntity(
                    id = UUID.randomUUID().toString(),
                    entityType = entityType.name,
                    entityId = entityId,
                    profileId = profileId,
                    reaction = reaction,
                    createdAt = System.currentTimeMillis(),
                ),
            )
        }
        AppResult.Success(Unit)
    } catch (t: Throwable) {
        AppResult.Failure(AppError.Unknown(t))
    }
}
