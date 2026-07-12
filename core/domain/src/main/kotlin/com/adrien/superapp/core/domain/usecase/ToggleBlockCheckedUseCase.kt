package com.adrien.superapp.core.domain.usecase

import com.adrien.superapp.core.common.AppResult
import com.adrien.superapp.core.domain.repository.BlockRepository
import javax.inject.Inject

class ToggleBlockCheckedUseCase @Inject constructor(
    private val blockRepository: BlockRepository,
) {
    suspend operator fun invoke(blockId: String): AppResult<Unit> = blockRepository.toggleChecked(blockId)
}
