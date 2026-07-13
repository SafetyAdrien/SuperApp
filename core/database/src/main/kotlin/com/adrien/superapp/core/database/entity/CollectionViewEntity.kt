package com.adrien.superapp.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "collection_views",
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
data class CollectionViewEntity(
    @PrimaryKey val id: String,
    val collectionId: String,
    val name: String,
    /** [com.adrien.superapp.core.model.CollectionViewType] serialized as its enum name. */
    val type: String,
    val position: Long,
    val sortPropertyId: String?,
    val sortDescending: Boolean,
    val groupPropertyId: String?,
    val filterPropertyId: String?,
    /** [com.adrien.superapp.core.model.FilterOperator] serialized as its enum name, or null. */
    val filterOperator: String?,
    val filterValue: String?,
)
