package com.adrien.superapp.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.adrien.superapp.core.database.entity.CollectionViewEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectionViewDao {

    @Query("SELECT * FROM collection_views WHERE collectionId = :collectionId ORDER BY position ASC")
    fun observeViewsForCollection(collectionId: String): Flow<List<CollectionViewEntity>>

    @Query("SELECT * FROM collection_views WHERE collectionId = :collectionId ORDER BY position ASC")
    suspend fun listForCollection(collectionId: String): List<CollectionViewEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(view: CollectionViewEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(views: List<CollectionViewEntity>)

    @Update
    suspend fun update(view: CollectionViewEntity)

    @Query("DELETE FROM collection_views WHERE id = :viewId")
    suspend fun delete(viewId: String)
}
