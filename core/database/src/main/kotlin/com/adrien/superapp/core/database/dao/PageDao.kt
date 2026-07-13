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

    /** Most recently updated pages across every space the profile is a member of — the workspace home's "Récents". */
    @Query(
        "SELECT p.* FROM pages p INNER JOIN space_members m ON m.spaceId = p.spaceId " +
            "WHERE m.profileId = :profileId AND p.archivedAt IS NULL " +
            "ORDER BY p.updatedAt DESC LIMIT :limit",
    )
    fun observeRecentPagesForMember(profileId: String, limit: Int): Flow<List<PageEntity>>

    /** A collection's entries — order is view-dependent, sorted/filtered in the domain layer (`CollectionViewEngine`). */
    @Query("SELECT * FROM pages WHERE collectionId = :collectionId AND archivedAt IS NULL ORDER BY createdAt ASC")
    fun observePagesForCollection(collectionId: String): Flow<List<PageEntity>>

    @Query("SELECT * FROM pages WHERE id = :pageId")
    fun observePage(pageId: String): Flow<PageEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(page: PageEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(pages: List<PageEntity>)

    @Update
    suspend fun update(page: PageEntity)

    @Query("DELETE FROM pages WHERE id = :pageId")
    suspend fun delete(pageId: String)

    @Query("SELECT COUNT(*) FROM pages")
    suspend fun count(): Int
}
