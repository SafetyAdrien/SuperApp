package com.adrien.superapp.core.domain.usecase

import com.adrien.superapp.core.common.AppResult
import com.adrien.superapp.core.domain.repository.BlockRepository
import javax.inject.Inject

class UpdateBlockContentUseCase @Inject constructor(
    private val blockRepository: BlockRepository,
) {
    suspend operator fun invoke(blockId: String, content: String): AppResult<Unit> =
        blockRepository.updateContent(blockId, content)
}
