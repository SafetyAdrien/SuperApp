package com.adrien.superapp.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.adrien.superapp.core.database.entity.CollectionPropertyEntity
import com.adrien.superapp.core.database.entity.CollectionPropertyOptionEntity
import kotlinx.coroutines.flow.Flow

/** Properties and their options — two small, tightly related tables, one DAO. */
@Dao
interface CollectionPropertyDao {

    @Query("SELECT * FROM collection_properties WHERE collectionId = :collectionId ORDER BY position ASC")
    fun observePropertiesForCollection(collectionId: String): Flow<List<CollectionPropertyEntity>>

    @Query("SELECT * FROM collection_properties WHERE collectionId = :collectionId ORDER BY position ASC")
    suspend fun listForCollection(collectionId: String): List<CollectionPropertyEntity>

    @Query("SELECT * FROM collection_properties WHERE id = :propertyId")
    suspend fun get(propertyId: String): CollectionPropertyEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(property: CollectionPropertyEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(properties: List<CollectionPropertyEntity>)

    @Update
    suspend fun update(property: CollectionPropertyEntity)

    @Query("DELETE FROM collection_properties WHERE id = :propertyId")
    suspend fun delete(propertyId: String)

    @Query("SELECT * FROM collection_property_options WHERE propertyId = :propertyId ORDER BY position ASC")
    fun observeOptionsForProperty(propertyId: String): Flow<List<CollectionPropertyOptionEntity>>

    @Query("SELECT * FROM collection_property_options WHERE propertyId = :propertyId ORDER BY position ASC")
    suspend fun listOptionsForProperty(propertyId: String): List<CollectionPropertyOptionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOption(option: CollectionPropertyOptionEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOptions(options: List<CollectionPropertyOptionEntity>)

    @Query("DELETE FROM collection_property_options WHERE id = :optionId")
    suspend fun deleteOption(optionId: String)
}
