package com.adrien.superapp.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "pages",
    foreignKeys = [
        ForeignKey(
            entity = SpaceEntity::class,
            parentColumns = ["id"],
            childColumns = ["spaceId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("spaceId"), Index("parentPageId"), Index("createdAt")],
)
data class PageEntity(
    @PrimaryKey val id: String,
    val spaceId: String,
    val parentPageId: String?,
    val title: String,
    val icon: String?,
    val coverUrl: String?,
    val coverColorKey: String?,
    val createdBy: String,
    val createdAt: Long,
    val updatedAt: Long,
    val archivedAt: Long?,
)
