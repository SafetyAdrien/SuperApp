package com.adrien.superapp.core.domain.usecase

import com.adrien.superapp.core.common.AppError
import com.adrien.superapp.core.common.AppResult
import com.adrien.superapp.core.domain.repository.CollectionRepository
import com.adrien.superapp.core.domain.repository.ProfileRepository
import com.adrien.superapp.core.model.Collection
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class CreateCollectionUseCase @Inject constructor(
    private val collectionRepository: CollectionRepository,
    private val profileRepository: ProfileRepository,
) {
    suspend operator fun invoke(spaceId: String, title: String = "Base de données sans titre"): AppResult<Collection> {
        val author = profileRepository.observeCurrentProfile().firstOrNull()
            ?: return AppResult.Failure(AppError.NotFound)

        return collectionRepository.createCollection(
            spaceId = spaceId,
            title = title.ifBlank { "Base de données sans titre" },
            createdBy = author.id,
        )
    }
}
