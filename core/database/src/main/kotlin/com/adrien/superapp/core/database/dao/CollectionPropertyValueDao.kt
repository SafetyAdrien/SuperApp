package com.adrien.superapp.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.adrien.superapp.core.database.entity.CollectionPropertyValueEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectionPropertyValueDao {

    @Query("SELECT * FROM collection_property_values WHERE pageId = :pageId")
    fun observeValuesForPage(pageId: String): Flow<List<CollectionPropertyValueEntity>>

    @Query(
        "SELECT v.* FROM collection_property_values v " +
            "INNER JOIN collection_properties p ON p.id = v.propertyId " +
            "WHERE p.collectionId = :collectionId",
    )
    fun observeValuesForCollection(collectionId: String): Flow<List<CollectionPropertyValueEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(value: CollectionPropertyValueEntity)

    @Query("DELETE FROM collection_property_values WHERE pageId = :pageId AND propertyId = :propertyId")
    suspend fun delete(pageId: String, propertyId: String)
}
