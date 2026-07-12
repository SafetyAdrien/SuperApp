package com.adrien.superapp.core.domain.usecase

import com.adrien.superapp.core.common.AppResult
import com.adrien.superapp.core.domain.repository.BlockRepository
import com.adrien.superapp.core.domain.repository.MoveDirection
import javax.inject.Inject

class MoveBlockUseCase @Inject constructor(
    private val blockRepository: BlockRepository,
) {
    suspend operator fun invoke(blockId: String, direction: MoveDirection): AppResult<Unit> =
        blockRepository.moveBlock(blockId, direction)
}
