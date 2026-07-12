package com.adrien.superapp.core.database.mapper

import com.adrien.superapp.core.database.entity.ProfileEntity
import com.adrien.superapp.core.model.Profile

fun ProfileEntity.toModel(): Profile = Profile(
    id = id,
    handle = handle,
    displayName = displayName,
    biography = biography,
    avatarUrl = avatarUrl,
    bannerUrl = bannerUrl,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun Profile.toEntity(): ProfileEntity = ProfileEntity(
    id = id,
    handle = handle,
    displayName = displayName,
    biography = biography,
    avatarUrl = avatarUrl,
    bannerUrl = bannerUrl,
    createdAt = createdAt,
    updatedAt = updatedAt,
)
