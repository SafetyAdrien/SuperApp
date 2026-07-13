package com.adrien.superapp.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "collection_properties",
    foreignKeys = [
        ForeignKey(
            entity = CollectionEntity::class,
            parentColumns = ["id"],
            childColumns = ["collectionId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("collectionId")],
)
data class CollectionPropertyEntity(
    @PrimaryKey val id: String,
    val collectionId: String,
    val name: String,
    /** [com.adrien.superapp.core.model.CollectionPropertyType] serialized as its enum name. */
    val type: String,
    val position: Long,
    val visible: Boolean,
)
