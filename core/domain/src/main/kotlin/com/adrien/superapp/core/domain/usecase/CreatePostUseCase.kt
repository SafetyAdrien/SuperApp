package com.adrien.superapp.core.domain.usecase

import com.adrien.superapp.core.common.AppError
import com.adrien.superapp.core.common.AppResult
import com.adrien.superapp.core.domain.repository.PostRepository
import com.adrien.superapp.core.domain.repository.ProfileRepository
import com.adrien.superapp.core.model.Post
import com.adrien.superapp.core.model.PostVisibility
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class CreatePostUseCase @Inject constructor(
    private val postRepository: PostRepository,
    private val profileRepository: ProfileRepository,
) {
    suspend operator fun invoke(
        text: String,
        visibility: PostVisibility = PostVisibility.PUBLIC,
        replyToPostId: String? = null,
    ): AppResult<Post> {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) {
            return AppResult.Failure(AppError.Unknown())
        }
        val author = profileRepository.observeCurrentProfile().firstOrNull()
            ?: return AppResult.Failure(AppError.NotFound)

        return postRepository.createPost(
            authorId = author.id,
            text = trimmed,
            visibility = visibility,
            replyToPostId = replyToPostId,
        )
    }
}
