package com.adrien.superapp.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.adrien.superapp.core.database.entity.ProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {

    /** In `LOCAL_DEMO`, the first (and only, for now) seeded profile is "current". */
    @Query("SELECT * FROM profiles ORDER BY createdAt ASC LIMIT 1")
    fun observeCurrentProfile(): Flow<ProfileEntity?>

    @Query("SELECT * FROM profiles WHERE id = :profileId")
    fun observeProfile(profileId: String): Flow<ProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(profile: ProfileEntity)

    @Update
    suspend fun update(profile: ProfileEntity)

    @Query("SELECT COUNT(*) FROM profiles")
    suspend fun count(): Int
}
