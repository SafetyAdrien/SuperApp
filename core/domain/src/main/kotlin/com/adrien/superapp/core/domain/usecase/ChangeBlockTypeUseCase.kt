package com.adrien.superapp.core.domain.usecase

import com.adrien.superapp.core.common.AppResult
import com.adrien.superapp.core.domain.repository.BlockRepository
import com.adrien.superapp.core.model.BlockType
import javax.inject.Inject

class ChangeBlockTypeUseCase @Inject constructor(
    private val blockRepository: BlockRepository,
) {
    suspend operator fun invoke(blockId: String, type: BlockType): AppResult<Unit> =
        blockRepository.updateType(blockId, type)
}
