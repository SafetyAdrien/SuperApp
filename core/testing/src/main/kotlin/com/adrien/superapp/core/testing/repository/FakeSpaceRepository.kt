package com.adrien.superapp.core.testing.repository

import com.adrien.superapp.core.common.AppError
import com.adrien.superapp.core.common.AppResult
import com.adrien.superapp.core.domain.repository.SpaceRepository
import com.adrien.superapp.core.model.Space
import com.adrien.superapp.core.model.SpaceVisibility
import com.adrien.superapp.core.model.SpaceWithStats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.util.UUID

class FakeSpaceRepository : SpaceRepository {

    private val spaces = MutableStateFlow<List<SpaceWithStats>>(emptyList())
    private var shouldFail = false

    fun setShouldFail(fail: Boolean) {
        shouldFail = fail
    }

    fun addSpace(space: SpaceWithStats) {
        spaces.value = spaces.value + space
    }

    override fun observeSpaces() = spaces.map { it }

    override fun observeSpace(spaceId: String) = spaces.map { list -> list.firstOrNull { it.space.id == spaceId } }

    override suspend fun createSpace(
        ownerId: String,
        name: String,
        description: String?,
        visibility: SpaceVisibility,
    ): AppResult<Space> {
        if (shouldFail) return AppResult.Failure(AppError.Unknown())

        val now = System.currentTimeMillis()
        val space = Space(
            id = UUID.randomUUID().toString(),
            name = name,
            description = description,
            ownerId = ownerId,
            visibility = visibility,
            createdAt = now,
            updatedAt = now,
        )
        addSpace(SpaceWithStats(space = space, memberCount = 1, pageCount = 0))
        return AppResult.Success(space)
    }
}
