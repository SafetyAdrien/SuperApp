package com.adrien.superapp.core.domain.repository

import androidx.paging.PagingData
import com.adrien.superapp.core.common.AppResult
import com.adrien.superapp.core.model.Post
import com.adrien.superapp.core.model.PostVisibility
import com.adrien.superapp.core.model.PostWithAuthor
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    /** The main feed ("Pour vous"): top-level posts (no `replyToPostId`), newest first. */
    fun observeFeed(): Flow<PagingData<PostWithAuthor>>

    fun observePost(postId: String): Flow<PostWithAuthor?>

    fun observeReplies(postId: String): Flow<List<PostWithAuthor>>

    suspend fun createPost(
        authorId: String,
        text: String,
        visibility: PostVisibility,
        replyToPostId: String? = null,
        quotedPostId: String? = null,
    ): AppResult<Post>
}
