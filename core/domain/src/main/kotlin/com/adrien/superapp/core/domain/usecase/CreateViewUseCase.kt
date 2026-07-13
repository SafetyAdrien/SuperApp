package com.adrien.superapp.core.domain.usecase

import com.adrien.superapp.core.common.AppError
import com.adrien.superapp.core.common.AppResult
import com.adrien.superapp.core.domain.repository.CollectionRepository
import com.adrien.superapp.core.model.CollectionView
import com.adrien.superapp.core.model.CollectionViewType
import javax.inject.Inject

class CreateViewUseCase @Inject constructor(
    private val collectionRepository: CollectionRepository,
) {
    suspend operator fun invoke(collectionId: String, name: String, type: CollectionViewType): AppResult<CollectionView> {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return AppResult.Failure(AppError.Unknown())

        return collectionRepository.createView(collectionId, trimmed, type)
    }
}
