package com.adrien.superapp.core.domain.usecase

import com.adrien.superapp.core.common.AppResult
import com.adrien.superapp.core.domain.repository.CollectionRepository
import javax.inject.Inject

class DeleteOptionUseCase @Inject constructor(
    private val collectionRepository: CollectionRepository,
) {
    suspend operator fun invoke(optionId: String): AppResult<Unit> = collectionRepository.deleteOption(optionId)
}
