package com.adrien.superapp.core.model

enum class PostVisibility {
    PUBLIC,
    SPACE,
    FOLLOWERS,
}

data class Post(
    val id: String,
    val authorId: String,
    val spaceId: String? = null,
    val text: String,
    val replyToPostId: String? = null,
    val quotedPostId: String? = null,
    val visibility: PostVisibility = PostVisibility.PUBLIC,
    val createdAt: Long,
    val updatedAt: Long,
)

/** A post joined with the data a feed row needs to render without N+1 queries. */
data class PostWithAuthor(
    val post: Post,
    val author: Profile,
    val reactionCount: Int,
    val replyCount: Int,
    val viewerHasReacted: Boolean,
)
