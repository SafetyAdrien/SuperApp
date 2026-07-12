package com.adrien.superapp.core.database.mapper

import com.adrien.superapp.core.database.entity.BlockEntity
import com.adrien.superapp.core.model.Block
import com.adrien.superapp.core.model.BlockType

fun BlockEntity.toModel(): Block = Block(
    id = id,
    pageId = pageId,
    parentBlockId = parentBlockId,
    type = runCatching { BlockType.valueOf(type) }.getOrDefault(BlockType.PARAGRAPH),
    position = position,
    content = content,
    checked = checked,
    language = language,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun Block.toEntity(): BlockEntity = BlockEntity(
    id = id,
    pageId = pageId,
    parentBlockId = parentBlockId,
    type = type.name,
    position = position,
    content = content,
    checked = checked,
    language = language,
    createdAt = createdAt,
    updatedAt = updatedAt,
)
