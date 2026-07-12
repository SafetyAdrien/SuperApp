package com.adrien.superapp.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.adrien.superapp.core.database.entity.BlockEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BlockDao {

    @Query("SELECT * FROM blocks WHERE pageId = :pageId ORDER BY position ASC")
    fun observeBlocksForPage(pageId: String): Flow<List<BlockEntity>>

    /** One-shot ordered read used to compute a new block's position or to swap two blocks' positions. */
    @Query("SELECT * FROM blocks WHERE pageId = :pageId ORDER BY position ASC")
    suspend fun listForPage(pageId: String): List<BlockEntity>

    @Query("SELECT * FROM blocks WHERE id = :blockId")
    suspend fun get(blockId: String): BlockEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(block: BlockEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(blocks: List<BlockEntity>)

    @Update
    suspend fun update(block: BlockEntity)

    @Query("DELETE FROM blocks WHERE id = :blockId")
    suspend fun delete(blockId: String)

    @Query("SELECT COUNT(*) FROM blocks")
    suspend fun count(): Int
}
