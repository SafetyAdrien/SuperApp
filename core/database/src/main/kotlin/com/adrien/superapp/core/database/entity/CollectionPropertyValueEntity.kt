package com.adrien.superapp.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/** One value per (entry page, property) pair — absent means empty, never stored as a blank row. */
@Entity(
    tableName = "collection_property_values",
    primaryKeys = ["pageId", "propertyId"],
    foreignKeys = [
        ForeignKey(
            entity = PageEntity::class,
            parentColumns = ["id"],
            childColumns = ["pageId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = CollectionPropertyEntity::class,
            parentColumns = ["id"],
            childColumns = ["propertyId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("pageId"), Index("propertyId")],
)
data class CollectionPropertyValueEntity(
    val pageId: String,
    val propertyId: String,
    val value: String?,
)
