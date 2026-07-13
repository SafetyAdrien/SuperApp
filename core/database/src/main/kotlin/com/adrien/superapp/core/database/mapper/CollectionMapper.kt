package com.adrien.superapp.core.database.mapper

import com.adrien.superapp.core.database.entity.CollectionEntity
import com.adrien.superapp.core.database.entity.CollectionPropertyEntity
import com.adrien.superapp.core.database.entity.CollectionPropertyOptionEntity
import com.adrien.superapp.core.database.entity.CollectionViewEntity
import com.adrien.superapp.core.model.Collection
import com.adrien.superapp.core.model.CollectionProperty
import com.adrien.superapp.core.model.CollectionPropertyOption
import com.adrien.superapp.core.model.CollectionPropertyType
import com.adrien.superapp.core.model.CollectionView
import com.adrien.superapp.core.model.CollectionViewType
import com.adrien.superapp.core.model.FilterOperator

fun CollectionEntity.toModel(): Collection = Collection(
    id = id,
    spaceId = spaceId,
    title = title,
    icon = icon,
    createdBy = createdBy,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun Collection.toEntity(): CollectionEntity = CollectionEntity(
    id = id,
    spaceId = spaceId,
    title = title,
    icon = icon,
    createdBy = createdBy,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun CollectionPropertyEntity.toModel(): CollectionProperty = CollectionProperty(
    id = id,
    collectionId = collectionId,
    name = name,
    type = runCatching { CollectionPropertyType.valueOf(type) }.getOrDefault(CollectionPropertyType.TEXT),
    position = position,
    visible = visible,
)

fun CollectionProperty.toEntity(): CollectionPropertyEntity = CollectionPropertyEntity(
    id = id,
    collectionId = collectionId,
    name = name,
    type = type.name,
    position = position,
    visible = visible,
)

fun CollectionPropertyOptionEntity.toModel(): CollectionPropertyOption = CollectionPropertyOption(
    id = id,
    propertyId = propertyId,
    label = label,
    colorKey = colorKey,
    position = position,
)

fun CollectionPropertyOption.toEntity(): CollectionPropertyOptionEntity = CollectionPropertyOptionEntity(
    id = id,
    propertyId = propertyId,
    label = label,
    colorKey = colorKey,
    position = position,
)

fun CollectionViewEntity.toModel(): CollectionView = CollectionView(
    id = id,
    collectionId = collectionId,
    name = name,
    type = runCatching { CollectionViewType.valueOf(type) }.getOrDefault(CollectionViewType.TABLE),
    position = position,
    sortPropertyId = sortPropertyId,
    sortDescending = sortDescending,
    groupPropertyId = groupPropertyId,
    filterPropertyId = filterPropertyId,
    filterOperator = filterOperator?.let { runCatching { FilterOperator.valueOf(it) }.getOrNull() },
    filterValue = filterValue,
)

fun CollectionView.toEntity(): CollectionViewEntity = CollectionViewEntity(
    id = id,
    collectionId = collectionId,
    name = name,
    type = type.name,
    position = position,
    sortPropertyId = sortPropertyId,
    sortDescending = sortDescending,
    groupPropertyId = groupPropertyId,
    filterPropertyId = filterPropertyId,
    filterOperator = filterOperator?.name,
    filterValue = filterValue,
)
