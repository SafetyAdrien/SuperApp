package com.adrien.superapp.core.domain.repository

import com.adrien.superapp.core.common.AppResult
import com.adrien.superapp.core.model.Block
import com.adrien.superapp.core.model.BlockType
import kotlinx.coroutines.flow.Flow

enum class MoveDirection { UP, DOWN }

interface BlockRepository {
    /** Blocks of a page, ordered by position. */
    fun observeBlocks(pageId: String): Flow<List<Block>>

    /** Inserts a block right after [afterBlockId], or at the end when null. */
    suspend fun createBlock(pageId: String, afterBlockId: String?, type: BlockType): AppResult<Block>

    suspend fun updateContent(blockId: String, content: String): AppResult<Unit>

    suspend fun toggleChecked(blockId: String): AppResult<Unit>

    suspend fun deleteBlock(blockId: String): AppResult<Unit>

    /** No-op when the block is already at that edge of the page. */
    suspend fun moveBlock(blockId: String, direction: MoveDirection): AppResult<Unit>
}
