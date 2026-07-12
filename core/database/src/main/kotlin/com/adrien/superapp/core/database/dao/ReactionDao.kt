package com.adrien.superapp.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.adrien.superapp.core.database.entity.ReactionEntity

@Dao
interface ReactionDao {

    @Query(
        "SELECT id FROM reactions WHERE entityType = :entityType AND entityId = :entityId " +
            "AND profileId = :profileId AND reaction = :reaction LIMIT 1",
    )
    suspend fun findId(entityType: String, entityId: String, profileId: String, reaction: String): String?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(reaction: ReactionEntity)

    @Query("DELETE FROM reactions WHERE id = :id")
    suspend fun deleteById(id: String)
}
