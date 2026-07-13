package com.adrien.superapp.core.domain.usecase

import com.adrien.superapp.core.common.AppError
import com.adrien.superapp.core.common.AppResult
import com.adrien.superapp.core.domain.repository.CollectionRepository
import com.adrien.superapp.core.model.CollectionProperty
import com.adrien.superapp.core.model.CollectionPropertyType
import javax.inject.Inject

class CreatePropertyUseCase @Inject constructor(
    private val collectionRepository: CollectionRepository,
) {
    suspend operator fun invoke(
        collectionId: String,
        name: String,
        type: CollectionPropertyType,
    ): AppResult<CollectionProperty> {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return AppResult.Failure(AppError.Unknown())

        return collectionRepository.createProperty(collectionId, trimmed, type)
    }
}
