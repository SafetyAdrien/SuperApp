package com.adrien.superapp.core.domain.usecase

import com.adrien.superapp.core.domain.repository.CollectionRepository
import com.adrien.superapp.core.model.Collection
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveCollectionUseCase @Inject constructor(
    private val collectionRepository: CollectionRepository,
) {
    operator fun invoke(collectionId: String): Flow<Collection?> = collectionRepository.observeCollection(collectionId)
}
