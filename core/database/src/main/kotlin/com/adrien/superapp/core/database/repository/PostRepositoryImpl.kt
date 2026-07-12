package com.adrien.superapp.core.database.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.adrien.superapp.core.common.AppError
import com.adrien.superapp.core.common.AppResult
import com.adrien.superapp.core.database.dao.PostDao
import com.adrien.superapp.core.database.entity.PostEntity
import com.adrien.superapp.core.database.mapper.toModel
import com.adrien.superapp.core.domain.repository.PostRepository
import com.adrien.superapp.core.domain.repository.ProfileRepository
import com.adrien.superapp.core.model.Post
import com.adrien.superapp.core.model.PostVisibility
import com.adrien.superapp.core.model.PostWithAuthor
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

private const val PAGE_SIZE = 20

@OptIn(ExperimentalCoroutinesApi::class)
@Singleton
class PostRepositoryImpl @Inject constructor(
    private val postDao: PostDao,
    private val profileRepository: ProfileRepository,
) : PostRepository {

    override fun observeFeed(): Flow<PagingData<PostWithAuthor>> =
        profileRepository.observeCurrentProfile().filterNotNull().flatMapLatest { viewer ->
            Pager(
                config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
                pagingSourceFactory = { postDao.pagingSource(viewer.id) },
            ).flow.map { pagingData -> pagingData.map { it.toModel() } }
        }

    override fun observePost(postId: String): Flow<PostWithAuthor?> =
        profileRepository.observeCurrentProfile().filterNotNull().flatMapLatest { viewer ->
            postDao.observePost(postId, viewer.id).map { it?.toModel() }
        }

    override fun observeReplies(postId: String): Flow<List<PostWithAuthor>> =
        profileRepository.observeCurrentProfile().filterNotNull().flatMapLatest { viewer ->
            postDao.observeReplies(postId, viewer.id).map { rows -> rows.map { it.toModel() } }
        }

    override suspend fun createPost(
        authorId: String,
        text: String,
        visibility: PostVisibility,
        replyToPostId: String?,
        quotedPostId: String?,
    ): AppResult<Post> = try {
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
        postDao.insert(
            PostEntity(
                id = post.id,
                authorId = post.authorId,
                spaceId = post.spaceId,
                text = post.text,
                replyToPostId = post.replyToPostId,
                quotedPostId = post.quotedPostId,
                visibility = post.visibility.name,
                createdAt = post.createdAt,
                updatedAt = post.updatedAt,
            ),
        )
        AppResult.Success(post)
    } catch (t: Throwable) {
        AppResult.Failure(AppError.Unknown(t))
    }
}
