package com.adrien.superapp.core.domain.usecase

import com.adrien.superapp.core.common.AppResult
import com.adrien.superapp.core.domain.repository.CollectionRepository
import javax.inject.Inject

class DeleteViewUseCase @Inject constructor(
    private val collectionRepository: CollectionRepository,
) {
    suspend operator fun invoke(viewId: String): AppResult<Unit> = collectionRepository.deleteView(viewId)
}
