package com.adrien.superapp.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "blocks",
    foreignKeys = [
        ForeignKey(
            entity = PageEntity::class,
            parentColumns = ["id"],
            childColumns = ["pageId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("pageId"), Index("parentBlockId"), Index("position")],
)
data class BlockEntity(
    @PrimaryKey val id: String,
    val pageId: String,
    val parentBlockId: String?,
    /** [com.adrien.superapp.core.model.BlockType] serialized as its enum name. */
    val type: String,
    val position: Long,
    val content: String,
    val checked: Boolean,
    val language: String?,
    val createdAt: Long,
    val updatedAt: Long,
)
