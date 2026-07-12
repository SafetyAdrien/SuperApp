package com.adrien.superapp.core.testing.repository

import com.adrien.superapp.core.common.AppError
import com.adrien.superapp.core.common.AppResult
import com.adrien.superapp.core.domain.repository.BlockRepository
import com.adrien.superapp.core.domain.repository.MoveDirection
import com.adrien.superapp.core.model.Block
import com.adrien.superapp.core.model.BlockType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.util.UUID

private const val POSITION_GAP = 1000L

class FakeBlockRepository : BlockRepository {

    private val blocks = MutableStateFlow<List<Block>>(emptyList())
    private var shouldFail = false

    fun setShouldFail(fail: Boolean) {
        shouldFail = fail
    }

    fun addBlock(block: Block) {
        blocks.value = blocks.value + block
    }

    override fun observeBlocks(pageId: String) = blocks.map { list ->
        list.filter { it.pageId == pageId }.sortedBy { it.position }
    }

    override suspend fun createBlock(pageId: String, afterBlockId: String?, type: BlockType): AppResult<Block> {
        if (shouldFail) return AppResult.Failure(AppError.Unknown())

        val siblings = blocks.value.filter { it.pageId == pageId }.sortedBy { it.position }
        val now = System.currentTimeMillis()
        val position = when {
            siblings.isEmpty() -> POSITION_GAP
            afterBlockId == null -> siblings.last().position + POSITION_GAP
            else -> {
                val index = siblings.indexOfFirst { it.id == afterBlockId }
                if (index == -1) {
                    siblings.last().position + POSITION_GAP
                } else {
                    val next = siblings.getOrNull(index + 1)
                    if (next != null) (siblings[index].position + next.position) / 2 else siblings[index].position + POSITION_GAP
                }
            }
        }
        val block = Block(
            id = UUID.randomUUID().toString(),
            pageId = pageId,
            type = type,
            position = position,
            createdAt = now,
            updatedAt = now,
        )
        addBlock(block)
        return AppResult.Success(block)
    }

    override suspend fun updateContent(blockId: String, content: String): AppResult<Unit> {
        if (shouldFail) return AppResult.Failure(AppError.Unknown())
        blocks.value = blocks.value.map { if (it.id == blockId) it.copy(content = content) else it }
        return AppResult.Success(Unit)
    }

    override suspend fun toggleChecked(blockId: String): AppResult<Unit> {
        if (shouldFail) return AppResult.Failure(AppError.Unknown())
        blocks.value = blocks.value.map { if (it.id == blockId) it.copy(checked = !it.checked) else it }
        return AppResult.Success(Unit)
    }

    override suspend fun deleteBlock(blockId: String): AppResult<Unit> {
        if (shouldFail) return AppResult.Failure(AppError.Unknown())
        blocks.value = blocks.value.filterNot { it.id == blockId }
        return AppResult.Success(Unit)
    }

    override suspend fun moveBlock(blockId: String, direction: MoveDirection): AppResult<Unit> {
        if (shouldFail) return AppResult.Failure(AppError.Unknown())

        val block = blocks.value.firstOrNull { it.id == blockId } ?: return AppResult.Failure(AppError.NotFound)
        val siblings = blocks.value.filter { it.pageId == block.pageId }.sortedBy { it.position }
        val index = siblings.indexOfFirst { it.id == blockId }
        val swapIndex = if (direction == MoveDirection.UP) index - 1 else index + 1
        if (swapIndex !in siblings.indices) return AppResult.Success(Unit)

        val current = siblings[index]
        val other = siblings[swapIndex]
        blocks.value = blocks.value.map {
            when (it.id) {
                current.id -> it.copy(position = other.position)
                other.id -> it.copy(position = current.position)
                else -> it
            }
        }
        return AppResult.Success(Unit)
    }
}
