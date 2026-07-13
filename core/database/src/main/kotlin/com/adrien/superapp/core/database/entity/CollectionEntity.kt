package com.adrien.superapp.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "collections",
    foreignKeys = [
        ForeignKey(
            entity = SpaceEntity::class,
            parentColumns = ["id"],
            childColumns = ["spaceId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("spaceId")],
)
data class CollectionEntity(
    @PrimaryKey val id: String,
    val spaceId: String,
    val title: String,
    val icon: String?,
    val createdBy: String,
    val createdAt: Long,
    val updatedAt: Long,
)
