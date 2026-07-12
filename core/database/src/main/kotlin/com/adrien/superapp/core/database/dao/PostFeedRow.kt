package com.adrien.superapp.core.database.dao

import androidx.room.ColumnInfo
import androidx.room.Embedded
import com.adrien.superapp.core.database.entity.PostEntity
import com.adrien.superapp.core.database.entity.ProfileEntity

/** Row shape for the joined feed/detail query — not a Room `@Entity`, just a query result POJO. */
data class PostFeedRow(
    @Embedded val post: PostEntity,
    @Embedded(prefix = "author_") val author: ProfileEntity,
    @ColumnInfo(name = "reactionCount") val reactionCount: Int,
    @ColumnInfo(name = "replyCount") val replyCount: Int,
    @ColumnInfo(name = "viewerHasReacted") val viewerHasReacted: Boolean,
)
