package com.adrien.superapp.core.domain.usecase

import com.adrien.superapp.core.domain.repository.CollectionRepository
import com.adrien.superapp.core.model.CollectionPropertyOption
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveOptionsUseCase @Inject constructor(
    private val collectionRepository: CollectionRepository,
) {
    operator fun invoke(propertyId: String): Flow<List<CollectionPropertyOption>> =
        collectionRepository.observeOptions(propertyId)
}
