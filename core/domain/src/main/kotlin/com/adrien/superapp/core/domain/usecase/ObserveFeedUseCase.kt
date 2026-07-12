package com.adrien.superapp.core.domain.usecase

import androidx.paging.PagingData
import com.adrien.superapp.core.domain.repository.PostRepository
import com.adrien.superapp.core.model.PostWithAuthor
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveFeedUseCase @Inject constructor(
    private val postRepository: PostRepository,
) {
    operator fun invoke(): Flow<PagingData<PostWithAuthor>> = postRepository.observeFeed()
}
