package com.adrien.superapp.core.database.mapper

import com.adrien.superapp.core.database.entity.PageEntity
import com.adrien.superapp.core.model.Page

fun PageEntity.toModel(): Page = Page(
    id = id,
    spaceId = spaceId,
    parentPageId = parentPageId,
    title = title,
    icon = icon,
    coverUrl = coverUrl,
    coverColorKey = coverColorKey,
    createdBy = createdBy,
    createdAt = createdAt,
    updatedAt = updatedAt,
    archivedAt = archivedAt,
)

fun Page.toEntity(): PageEntity = PageEntity(
    id = id,
    spaceId = spaceId,
    parentPageId = parentPageId,
    title = title,
    icon = icon,
    coverUrl = coverUrl,
    coverColorKey = coverColorKey,
    createdBy = createdBy,
    createdAt = createdAt,
    updatedAt = updatedAt,
    archivedAt = archivedAt,
)
