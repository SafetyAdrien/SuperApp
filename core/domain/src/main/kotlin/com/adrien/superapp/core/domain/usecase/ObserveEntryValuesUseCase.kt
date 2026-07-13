package com.adrien.superapp.core.domain.usecase

import com.adrien.superapp.core.domain.repository.CollectionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveEntryValuesUseCase @Inject constructor(
    private val collectionRepository: CollectionRepository,
) {
    operator fun invoke(pageId: String): Flow<Map<String, String>> = collectionRepository.observeValuesForPage(pageId)
}
