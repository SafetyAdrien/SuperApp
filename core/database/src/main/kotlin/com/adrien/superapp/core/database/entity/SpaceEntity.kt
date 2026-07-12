package com.adrien.superapp.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "spaces")
data class SpaceEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String?,
    val icon: String?,
    val ownerId: String,
    /** [com.adrien.superapp.core.model.SpaceVisibility] serialized as its enum name. */
    val visibility: String,
    val createdAt: Long,
    val updatedAt: Long,
)
