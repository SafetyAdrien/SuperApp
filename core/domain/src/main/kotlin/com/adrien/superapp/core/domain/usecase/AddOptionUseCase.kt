package com.adrien.superapp.core.domain.usecase

import com.adrien.superapp.core.common.AppError
import com.adrien.superapp.core.common.AppResult
import com.adrien.superapp.core.domain.repository.CollectionRepository
import com.adrien.superapp.core.model.CollectionPropertyOption
import javax.inject.Inject

class AddOptionUseCase @Inject constructor(
    private val collectionRepository: CollectionRepository,
) {
    suspend operator fun invoke(propertyId: String, label: String, colorKey: String): AppResult<CollectionPropertyOption> {
        val trimmed = label.trim()
        if (trimmed.isEmpty()) return AppResult.Failure(AppError.Unknown())

        return collectionRepository.addOption(propertyId, trimmed, colorKey)
    }
}
