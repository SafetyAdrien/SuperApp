package com.adrien.superapp.core.database.repository

import com.adrien.superapp.core.common.AppError
import com.adrien.superapp.core.common.AppResult
import com.adrien.superapp.core.database.dao.SpaceDao
import com.adrien.superapp.core.database.entity.SpaceEntity
import com.adrien.superapp.core.database.entity.SpaceMemberEntity
import com.adrien.superapp.core.database.mapper.toModel
import com.adrien.superapp.core.domain.repository.ProfileRepository
import com.adrien.superapp.core.domain.repository.SpaceRepository
import com.adrien.superapp.core.model.Space
import com.adrien.superapp.core.model.SpaceMemberRole
import com.adrien.superapp.core.model.SpaceVisibility
import com.adrien.superapp.core.model.SpaceWithStats
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(ExperimentalCoroutinesApi::class)
@Singleton
class SpaceRepositoryImpl @Inject constructor(
    private val spaceDao: SpaceDao,
    private val profileRepository: ProfileRepository,
) : SpaceRepository {

    override fun observeSpaces(): Flow<List<SpaceWithStats>> =
        profileRepository.observeCurrentProfile().filterNotNull().flatMapLatest { viewer ->
            spaceDao.observeSpacesForMember(viewer.id).map { rows -> rows.map { it.toModel() } }
        }

    override fun observeSpace(spaceId: String): Flow<SpaceWithStats?> =
        spaceDao.observeSpace(spaceId).map { it?.toModel() }

    override suspend fun createSpace(
        ownerId: String,
        name: String,
        description: String?,
        visibility: SpaceVisibility,
    ): AppResult<Space> = try {
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
        spaceDao.insert(
            SpaceEntity(
                id = space.id,
                name = space.name,
                description = space.description,
                icon = space.icon,
                ownerId = space.ownerId,
                visibility = space.visibility.name,
                createdAt = space.createdAt,
                updatedAt = space.updatedAt,
            ),
        )
        spaceDao.insertMember(
            SpaceMemberEntity(
                spaceId = space.id,
                profileId = ownerId,
                role = SpaceMemberRole.OWNER.name,
                joinedAt = now,
            ),
        )
        AppResult.Success(space)
    } catch (t: Throwable) {
        AppResult.Failure(AppError.Unknown(t))
    }
}
