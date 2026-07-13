package com.adrien.superapp.core.domain.usecase

import com.adrien.superapp.core.common.AppResult
import com.adrien.superapp.core.domain.repository.CollectionRepository
import javax.inject.Inject

class SetPropertyValueUseCase @Inject constructor(
    private val collectionRepository: CollectionRepository,
) {
    suspend operator fun invoke(pageId: String, propertyId: String, value: String?): AppResult<Unit> =
        collectionRepository.setPropertyValue(pageId, propertyId, value)
}
