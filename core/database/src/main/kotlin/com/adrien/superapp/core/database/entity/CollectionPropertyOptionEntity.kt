package com.adrien.superapp.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "collection_property_options",
    foreignKeys = [
        ForeignKey(
            entity = CollectionPropertyEntity::class,
            parentColumns = ["id"],
            childColumns = ["propertyId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("propertyId")],
)
data class CollectionPropertyOptionEntity(
    @PrimaryKey val id: String,
    val propertyId: String,
    val label: String,
    val colorKey: String,
    val position: Long,
)
