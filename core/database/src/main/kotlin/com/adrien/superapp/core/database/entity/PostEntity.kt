package com.adrien.superapp.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "posts",
    foreignKeys = [
        ForeignKey(
            entity = ProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["authorId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index("authorId"),
        Index("replyToPostId"),
        Index("createdAt"),
    ],
)
data class PostEntity(
    @PrimaryKey val id: String,
    val authorId: String,
    val spaceId: String?,
    val text: String,
    val replyToPostId: String?,
    val quotedPostId: String?,
    /** [com.adrien.superapp.core.model.PostVisibility] serialized as its enum name. */
    val visibility: String,
    val createdAt: Long,
    val updatedAt: Long,
)
