package com.adrien.superapp.core.domain.usecase

import com.adrien.superapp.core.common.AppResult
import com.adrien.superapp.core.domain.repository.CollectionRepository
import javax.inject.Inject

class SetPropertyVisibleUseCase @Inject constructor(
    private val collectionRepository: CollectionRepository,
) {
    suspend operator fun invoke(propertyId: String, visible: Boolean): AppResult<Unit> =
        collectionRepository.setPropertyVisible(propertyId, visible)
}
