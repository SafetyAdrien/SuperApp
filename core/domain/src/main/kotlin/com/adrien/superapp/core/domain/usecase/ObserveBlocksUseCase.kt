package com.adrien.superapp.core.domain.usecase

import com.adrien.superapp.core.domain.repository.BlockRepository
import com.adrien.superapp.core.model.Block
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveBlocksUseCase @Inject constructor(
    private val blockRepository: BlockRepository,
) {
    operator fun invoke(pageId: String): Flow<List<Block>> = blockRepository.observeBlocks(pageId)
}
