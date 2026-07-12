package com.adrien.superapp.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.adrien.superapp.core.database.entity.PageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PageDao {

    /** Top-level pages only — nested sub-pages land once the block editor can navigate into them. */
    @Query(
        "SELECT * FROM pages WHERE spaceId = :spaceId AND parentPageId IS NULL " +
            "AND archivedAt IS NULL ORDER BY createdAt ASC",
    )
    fun observePagesForSpace(spaceId: String): Flow<List<PageEntity>>

    @Query("SELECT * FROM pages WHERE id = :pageId")
    fun observePage(pageId: String): Flow<PageEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(page: PageEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(pages: List<PageEntity>)

    @Update
    suspend fun update(page: PageEntity)

    @Query("SELECT COUNT(*) FROM pages")
    suspend fun count(): Int
}
