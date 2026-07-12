package com.adrien.superapp.core.database.mapper

import com.adrien.superapp.core.database.dao.SpaceWithStatsRow
import com.adrien.superapp.core.database.entity.SpaceEntity
import com.adrien.superapp.core.database.entity.SpaceMemberEntity
import com.adrien.superapp.core.model.Space
import com.adrien.superapp.core.model.SpaceMember
import com.adrien.superapp.core.model.SpaceVisibility
import com.adrien.superapp.core.model.SpaceWithStats

fun SpaceEntity.toModel(): Space = Space(
    id = id,
    name = name,
    description = description,
    icon = icon,
    ownerId = ownerId,
    visibility = runCatching { SpaceVisibility.valueOf(visibility) }.getOrDefault(SpaceVisibility.PRIVATE),
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun Space.toEntity(): SpaceEntity = SpaceEntity(
    id = id,
    name = name,
    description = description,
    icon = icon,
    ownerId = ownerId,
    visibility = visibility.name,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun SpaceWithStatsRow.toModel(): SpaceWithStats = SpaceWithStats(
    space = space.toModel(),
    memberCount = memberCount,
    pageCount = pageCount,
)

fun SpaceMember.toEntity(): SpaceMemberEntity = SpaceMemberEntity(
    spaceId = spaceId,
    profileId = profileId,
    role = role.name,
    joinedAt = joinedAt,
)
