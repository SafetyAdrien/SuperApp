package com.adrien.superapp.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "space_members",
    primaryKeys = ["spaceId", "profileId"],
    foreignKeys = [
        ForeignKey(
            entity = SpaceEntity::class,
            parentColumns = ["id"],
            childColumns = ["spaceId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = ProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["profileId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("spaceId"), Index("profileId")],
)
data class SpaceMemberEntity(
    val spaceId: String,
    val profileId: String,
    /** [com.adrien.superapp.core.model.SpaceMemberRole] serialized as its enum name. */
    val role: String,
    val joinedAt: Long,
)
