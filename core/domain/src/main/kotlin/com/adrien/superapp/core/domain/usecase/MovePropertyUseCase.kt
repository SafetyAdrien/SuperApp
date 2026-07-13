package com.adrien.superapp.core.domain.usecase

import com.adrien.superapp.core.common.AppResult
import com.adrien.superapp.core.domain.repository.CollectionRepository
import com.adrien.superapp.core.domain.repository.MoveDirection
import javax.inject.Inject

class MovePropertyUseCase @Inject constructor(
    private val collectionRepository: CollectionRepository,
) {
    suspend operator fun invoke(propertyId: String, direction: MoveDirection): AppResult<Unit> =
        collectionRepository.moveProperty(propertyId, direction)
}
