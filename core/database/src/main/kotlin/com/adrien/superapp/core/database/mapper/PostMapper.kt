package com.adrien.superapp.core.database.mapper

import com.adrien.superapp.core.database.dao.PostFeedRow
import com.adrien.superapp.core.database.entity.PostEntity
import com.adrien.superapp.core.model.Post
import com.adrien.superapp.core.model.PostVisibility
import com.adrien.superapp.core.model.PostWithAuthor

fun PostEntity.toModel(): Post = Post(
    id = id,
    authorId = authorId,
    spaceId = spaceId,
    text = text,
    replyToPostId = replyToPostId,
    quotedPostId = quotedPostId,
    visibility = runCatching { PostVisibility.valueOf(visibility) }.getOrDefault(PostVisibility.PUBLIC),
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun Post.toEntity(): PostEntity = PostEntity(
    id = id,
    authorId = authorId,
    spaceId = spaceId,
    text = text,
    replyToPostId = replyToPostId,
    quotedPostId = quotedPostId,
    visibility = visibility.name,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun PostFeedRow.toModel(): PostWithAuthor = PostWithAuthor(
    post = post.toModel(),
    author = author.toModel(),
    reactionCount = reactionCount,
    replyCount = replyCount,
    viewerHasReacted = viewerHasReacted,
)
