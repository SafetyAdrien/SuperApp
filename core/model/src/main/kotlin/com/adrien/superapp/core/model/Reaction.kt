package com.adrien.superapp.core.model

/** What kind of entity a polymorphic reference (reaction, notification, ...) points at. */
enum class EntityType {
    POST,
    PAGE,
    BLOCK,
    TASK,
    CANVAS,
    MESSAGE,
    COMMENT,
}

/** Reactions are polymorphic over (entityType, entityId) rather than one table per parent. */
data class Reaction(
    val id: String,
    val entityType: EntityType,
    val entityId: String,
    val profileId: String,
    val reaction: String,
    val createdAt: Long,
)
