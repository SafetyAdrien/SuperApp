package com.adrien.superapp.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "reactions",
    indices = [
        Index(value = ["entityType", "entityId"]),
        Index(value = ["entityType", "entityId", "profileId", "reaction"], unique = true),
    ],
)
data class ReactionEntity(
    @PrimaryKey val id: String,
    /** [com.adrien.superapp.core.model.EntityType] serialized as its enum name. */
    val entityType: String,
    val entityId: String,
    val profileId: String,
    val reaction: String,
    val createdAt: Long,
)
