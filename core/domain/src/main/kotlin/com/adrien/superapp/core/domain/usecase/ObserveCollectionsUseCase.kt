package com.adrien.superapp.core.domain.usecase

import com.adrien.superapp.core.domain.repository.CollectionRepository
import com.adrien.superapp.core.model.Collection
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveCollectionsUseCase @Inject constructor(
    private val collectionRepository: CollectionRepository,
) {
    operator fun invoke(spaceId: String): Flow<List<Collection>> = collectionRepository.observeCollections(spaceId)
}
