package com.adrien.superapp.core.domain.usecase

import com.adrien.superapp.core.domain.repository.CollectionRepository
import com.adrien.superapp.core.model.CollectionView
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveViewsUseCase @Inject constructor(
    private val collectionRepository: CollectionRepository,
) {
    operator fun invoke(collectionId: String): Flow<List<CollectionView>> = collectionRepository.observeViews(collectionId)
}
