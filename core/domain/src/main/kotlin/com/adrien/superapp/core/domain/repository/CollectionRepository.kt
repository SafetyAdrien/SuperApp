package com.adrien.superapp.core.domain.repository

import com.adrien.superapp.core.common.AppResult
import com.adrien.superapp.core.model.Collection
import com.adrien.superapp.core.model.CollectionEntry
import com.adrien.superapp.core.model.CollectionProperty
import com.adrien.superapp.core.model.CollectionPropertyOption
import com.adrien.superapp.core.model.CollectionPropertyType
import com.adrien.superapp.core.model.CollectionView
import com.adrien.superapp.core.model.CollectionViewType
import kotlinx.coroutines.flow.Flow

interface CollectionRepository {

    fun observeCollections(spaceId: String): Flow<List<Collection>>
    fun observeCollection(collectionId: String): Flow<Collection?>
    suspend fun createCollection(spaceId: String, title: String, createdBy: String): AppResult<Collection>

    fun observeProperties(collectionId: String): Flow<List<CollectionProperty>>
    suspend fun createProperty(collectionId: String, name: String, type: CollectionPropertyType): AppResult<CollectionProperty>
    suspend fun renameProperty(propertyId: String, name: String): AppResult<Unit>
    suspend fun setPropertyVisible(propertyId: String, visible: Boolean): AppResult<Unit>

    /** No-op when the property is already at that edge of the collection's property list. */
    suspend fun moveProperty(propertyId: String, direction: MoveDirection): AppResult<Unit>
    suspend fun deleteProperty(propertyId: String): AppResult<Unit>

    fun observeOptions(propertyId: String): Flow<List<CollectionPropertyOption>>
    suspend fun addOption(propertyId: String, label: String, colorKey: String): AppResult<CollectionPropertyOption>
    suspend fun deleteOption(optionId: String): AppResult<Unit>

    /** Entries (pages) of a collection, unsorted/unfiltered — apply a view with `CollectionViewEngine`. */
    fun observeEntries(collectionId: String): Flow<List<CollectionEntry>>

    /** A single entry's property values — lighter than [observeEntries] for a page-detail screen. */
    fun observeValuesForPage(pageId: String): Flow<Map<String, String>>

    /** Setting a blank/null value deletes the underlying row rather than storing an empty string. */
    suspend fun setPropertyValue(pageId: String, propertyId: String, value: String?): AppResult<Unit>

    fun observeViews(collectionId: String): Flow<List<CollectionView>>
    suspend fun createView(collectionId: String, name: String, type: CollectionViewType): AppResult<CollectionView>
    suspend fun updateView(view: CollectionView): AppResult<Unit>
    suspend fun deleteView(viewId: String): AppResult<Unit>
}
