package com.adrien.superapp.core.domain.usecase

import com.adrien.superapp.core.common.AppError
import com.adrien.superapp.core.common.AppResult
import com.adrien.superapp.core.domain.repository.PageRepository
import com.adrien.superapp.core.domain.repository.ProfileRepository
import com.adrien.superapp.core.model.Page
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class CreatePageUseCase @Inject constructor(
    private val pageRepository: PageRepository,
    private val profileRepository: ProfileRepository,
) {
    suspend operator fun invoke(
        spaceId: String,
        title: String = "Page sans titre",
        collectionId: String? = null,
    ): AppResult<Page> {
        val author = profileRepository.observeCurrentProfile().firstOrNull()
            ?: return AppResult.Failure(AppError.NotFound)

        return pageRepository.createPage(
            spaceId = spaceId,
            title = title.ifBlank { "Page sans titre" },
            createdBy = author.id,
            collectionId = collectionId,
        )
    }
}
