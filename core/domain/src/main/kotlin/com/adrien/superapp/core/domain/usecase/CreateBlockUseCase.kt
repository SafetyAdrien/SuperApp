package com.adrien.superapp.core.domain.usecase

import com.adrien.superapp.core.common.AppResult
import com.adrien.superapp.core.domain.repository.BlockRepository
import com.adrien.superapp.core.model.Block
import com.adrien.superapp.core.model.BlockType
import javax.inject.Inject

class CreateBlockUseCase @Inject constructor(
    private val blockRepository: BlockRepository,
) {
    suspend operator fun invoke(
        pageId: String,
        afterBlockId: String? = null,
        type: BlockType = BlockType.PARAGRAPH,
    ): AppResult<Block> = blockRepository.createBlock(pageId, afterBlockId, type)
}
