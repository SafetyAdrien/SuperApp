package com.adrien.superapp.core.domain.repository

import com.adrien.superapp.core.common.AppResult
import com.adrien.superapp.core.model.Space
import com.adrien.superapp.core.model.SpaceVisibility
import com.adrien.superapp.core.model.SpaceWithStats
import kotlinx.coroutines.flow.Flow

interface SpaceRepository {
    /** Spaces the current viewer is a member of, most recently active first. */
    fun observeSpaces(): Flow<List<SpaceWithStats>>

    fun observeSpace(spaceId: String): Flow<SpaceWithStats?>

    suspend fun createSpace(
        ownerId: String,
        name: String,
        description: String?,
        visibility: SpaceVisibility,
    ): AppResult<Space>
}
