package com.adrien.superapp.core.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.adrien.superapp.core.database.entity.PostEntity
import kotlinx.coroutines.flow.Flow

private const val FEED_ROW_SELECT = """
    SELECT
        p.*,
        a.id AS author_id, a.handle AS author_handle, a.displayName AS author_displayName,
        a.biography AS author_biography, a.avatarUrl AS author_avatarUrl,
        a.bannerUrl AS author_bannerUrl, a.createdAt AS author_createdAt, a.updatedAt AS author_updatedAt,
        (SELECT COUNT(*) FROM reactions r WHERE r.entityType = 'POST' AND r.entityId = p.id) AS reactionCount,
        (SELECT COUNT(*) FROM posts rep WHERE rep.replyToPostId = p.id) AS replyCount,
        (
            SELECT COUNT(*) > 0 FROM reactions r2
            WHERE r2.entityType = 'POST' AND r2.entityId = p.id AND r2.profileId = :viewerId
        ) AS viewerHasReacted
    FROM posts p
    INNER JOIN profiles a ON a.id = p.authorId
"""

@Dao
interface PostDao {

    @Query("$FEED_ROW_SELECT WHERE p.replyToPostId IS NULL ORDER BY p.createdAt DESC")
    fun pagingSource(viewerId: String): PagingSource<Int, PostFeedRow>

    @Query("$FEED_ROW_SELECT WHERE p.id = :postId")
    fun observePost(postId: String, viewerId: String): Flow<PostFeedRow?>

    @Query("$FEED_ROW_SELECT WHERE p.replyToPostId = :postId ORDER BY p.createdAt ASC")
    fun observeReplies(postId: String, viewerId: String): Flow<List<PostFeedRow>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: PostEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(posts: List<PostEntity>)

    @Query("SELECT COUNT(*) FROM posts")
    suspend fun count(): Int
}
