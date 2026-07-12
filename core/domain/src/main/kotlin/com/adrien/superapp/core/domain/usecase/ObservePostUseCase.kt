package com.adrien.superapp.core.domain.usecase

import com.adrien.superapp.core.domain.repository.PostRepository
import com.adrien.superapp.core.model.PostWithAuthor
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObservePostUseCase @Inject constructor(
    private val postRepository: PostRepository,
) {
    operator fun invoke(postId: String): Flow<PostWithAuthor?> = postRepository.observePost(postId)
}
