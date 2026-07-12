package com.adrien.superapp.core.testing.repository

import androidx.paging.PagingData
import com.adrien.superapp.core.common.AppError
import com.adrien.superapp.core.common.AppResult
import com.adrien.superapp.core.domain.repository.PostRepository
import com.adrien.superapp.core.model.Post
import com.adrien.superapp.core.model.PostVisibility
import com.adrien.superapp.core.model.PostWithAuthor
import com.adrien.superapp.core.model.Profile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.util.UUID

class FakePostRepository : PostRepository {

    private val posts = MutableStateFlow<List<PostWithAuthor>>(emptyList())
    private var shouldFail = false

    fun setShouldFail(fail: Boolean) {
        shouldFail = fail
    }

    fun addPost(post: PostWithAuthor) {
        posts.value = posts.value + post
    }

    override fun observeFeed() = posts.map { list ->
        PagingData.from(list.filter { it.post.replyToPostId == null })
    }

    override fun observePost(postId: String) = posts.map { list -> list.firstOrNull { it.post.id == postId } }

    override fun observeReplies(postId: String) = posts.map { list ->
        list.filter { it.post.replyToPostId == postId }
    }

    override suspend fun createPost(
        authorId: String,
        text: String,
        visibility: PostVisibility,
        replyToPostId: String?,
        quotedPostId: String?,
    ): AppResult<Post> {
        if (shouldFail) return AppResult.Failure(AppError.Unknown())

        val now = System.currentTimeMillis()
        val post = Post(
            id = UUID.randomUUID().toString(),
            authorId = authorId,
            text = text,
            replyToPostId = replyToPostId,
            quotedPostId = quotedPostId,
            visibility = visibility,
            createdAt = now,
            updatedAt = now,
        )
        addPost(
            PostWithAuthor(
                post = post,
                author = Profile(
                    id = authorId,
                    handle = "fake",
                    displayName = "Fake",
                    createdAt = now,
                    updatedAt = now,
                ),
                reactionCount = 0,
                replyCount = 0,
                viewerHasReacted = false,
            ),
        )
        return AppResult.Success(post)
    }
}
