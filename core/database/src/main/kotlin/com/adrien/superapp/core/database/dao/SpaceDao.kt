package com.adrien.superapp.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.adrien.superapp.core.database.entity.SpaceEntity
import com.adrien.superapp.core.database.entity.SpaceMemberEntity
import kotlinx.coroutines.flow.Flow

private const val SPACE_WITH_STATS_SELECT = """
    SELECT
        s.*,
        (SELECT COUNT(*) FROM space_members sm WHERE sm.spaceId = s.id) AS memberCount,
        (SELECT COUNT(*) FROM pages p WHERE p.spaceId = s.id AND p.archivedAt IS NULL) AS pageCount
    FROM spaces s
"""

@Dao
interface SpaceDao {

    @Query(
        "$SPACE_WITH_STATS_SELECT INNER JOIN space_members m ON m.spaceId = s.id " +
            "WHERE m.profileId = :profileId ORDER BY s.updatedAt DESC",
    )
    fun observeSpacesForMember(profileId: String): Flow<List<SpaceWithStatsRow>>

    @Query("$SPACE_WITH_STATS_SELECT WHERE s.id = :spaceId")
    fun observeSpace(spaceId: String): Flow<SpaceWithStatsRow?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(space: SpaceEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(spaces: List<SpaceEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: SpaceMemberEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMembers(members: List<SpaceMemberEntity>)

    @Query("SELECT COUNT(*) FROM spaces")
    suspend fun count(): Int
}
