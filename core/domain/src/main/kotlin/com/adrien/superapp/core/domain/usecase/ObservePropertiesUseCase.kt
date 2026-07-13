package com.adrien.superapp.core.domain.usecase

import com.adrien.superapp.core.domain.repository.CollectionRepository
import com.adrien.superapp.core.model.CollectionProperty
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObservePropertiesUseCase @Inject constructor(
    private val collectionRepository: CollectionRepository,
) {
    operator fun invoke(collectionId: String): Flow<List<CollectionProperty>> =
        collectionRepository.observeProperties(collectionId)
}
