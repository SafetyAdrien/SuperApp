package com.adrien.superapp.core.database.repository

import com.adrien.superapp.core.common.AppError
import com.adrien.superapp.core.common.AppResult
import com.adrien.superapp.core.database.dao.BlockDao
import com.adrien.superapp.core.database.entity.BlockEntity
import com.adrien.superapp.core.database.mapper.toEntity
import com.adrien.superapp.core.database.mapper.toModel
import com.adrien.superapp.core.domain.repository.BlockRepository
import com.adrien.superapp.core.domain.repository.MoveDirection
import com.adrien.superapp.core.model.Block
import com.adrien.superapp.core.model.BlockType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/** Gap between two adjacent blocks' positions, large enough for many inserts-between before collapsing. */
private const val POSITION_GAP = 1000L

@Singleton
class BlockRepositoryImpl @Inject constructor(
    private val blockDao: BlockDao,
) : BlockRepository {

    override fun observeBlocks(pageId: String): Flow<List<Block>> =
        blockDao.observeBlocksForPage(pageId).map { rows -> rows.map { it.toModel() } }

    override suspend fun createBlock(pageId: String, afterBlockId: String?, type: BlockType): AppResult<Block> = try {
        val siblings = blockDao.listForPage(pageId)
        val now = System.currentTimeMillis()
        val position = nextPosition(siblings, afterBlockId)
        val block = Block(
            id = UUID.randomUUID().toString(),
            pageId = pageId,
            type = type,
            position = position,
            createdAt = now,
            updatedAt = now,
        )
        blockDao.insert(block.toEntity())
        AppResult.Success(block)
    } catch (t: Throwable) {
        AppResult.Failure(AppError.Unknown(t))
    }

    override suspend fun updateContent(blockId: String, content: String): AppResult<Unit> = try {
        val existing = blockDao.get(blockId) ?: return AppResult.Failure(AppError.NotFound)
        blockDao.update(existing.copy(content = content, updatedAt = System.currentTimeMillis()))
        AppResult.Success(Unit)
    } catch (t: Throwable) {
        AppResult.Failure(AppError.Unknown(t))
    }

    override suspend fun toggleChecked(blockId: String): AppResult<Unit> = try {
        val existing = blockDao.get(blockId) ?: return AppResult.Failure(AppError.NotFound)
        blockDao.update(existing.copy(checked = !existing.checked, updatedAt = System.currentTimeMillis()))
        AppResult.Success(Unit)
    } catch (t: Throwable) {
        AppResult.Failure(AppError.Unknown(t))
    }

    override suspend fun deleteBlock(blockId: String): AppResult<Unit> = try {
        blockDao.delete(blockId)
        AppResult.Success(Unit)
    } catch (t: Throwable) {
        AppResult.Failure(AppError.Unknown(t))
    }

    override suspend fun moveBlock(blockId: String, direction: MoveDirection): AppResult<Unit> = try {
        val block = blockDao.get(blockId) ?: return AppResult.Failure(AppError.NotFound)
        val siblings = blockDao.listForPage(block.pageId)
        val index = siblings.indexOfFirst { it.id == blockId }
        val swapIndex = if (direction == MoveDirection.UP) index - 1 else index + 1
        if (index == -1 || swapIndex !in siblings.indices) {
            AppResult.Success(Unit)
        } else {
            val current = siblings[index]
            val other = siblings[swapIndex]
            val now = System.currentTimeMillis()
            blockDao.update(current.copy(position = other.position, updatedAt = now))
            blockDao.update(other.copy(position = current.position, updatedAt = now))
            AppResult.Success(Unit)
        }
    } catch (t: Throwable) {
        AppResult.Failure(AppError.Unknown(t))
    }

    private fun nextPosition(
        siblings: List<BlockEntity>,
        afterBlockId: String?,
    ): Long {
        if (siblings.isEmpty()) return POSITION_GAP
        if (afterBlockId == null) return siblings.last().position + POSITION_GAP

        val afterIndex = siblings.indexOfFirst { it.id == afterBlockId }
        if (afterIndex == -1) return siblings.last().position + POSITION_GAP

        val afterPosition = siblings[afterIndex].position
        val nextPosition = siblings.getOrNull(afterIndex + 1)?.position
        return if (nextPosition != null) (afterPosition + nextPosition) / 2 else afterPosition + POSITION_GAP
    }
}
