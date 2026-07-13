package com.adrien.superapp.core.database.repository

import com.adrien.superapp.core.common.AppError
import com.adrien.superapp.core.common.AppResult
import com.adrien.superapp.core.database.dao.CollectionDao
import com.adrien.superapp.core.database.dao.CollectionPropertyDao
import com.adrien.superapp.core.database.dao.CollectionPropertyValueDao
import com.adrien.superapp.core.database.dao.CollectionViewDao
import com.adrien.superapp.core.database.dao.PageDao
import com.adrien.superapp.core.database.entity.CollectionPropertyValueEntity
import com.adrien.superapp.core.database.mapper.toEntity
import com.adrien.superapp.core.database.mapper.toModel
import com.adrien.superapp.core.domain.repository.CollectionRepository
import com.adrien.superapp.core.domain.repository.MoveDirection
import com.adrien.superapp.core.model.Collection
import com.adrien.superapp.core.model.CollectionEntry
import com.adrien.superapp.core.model.CollectionProperty
import com.adrien.superapp.core.model.CollectionPropertyOption
import com.adrien.superapp.core.model.CollectionPropertyType
import com.adrien.superapp.core.model.CollectionView
import com.adrien.superapp.core.model.CollectionViewType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/** Gap between siblings' positions — same scheme as `BlockRepositoryImpl`, see docs/DECISIONS.md. */
private const val POSITION_GAP = 1000L

@Singleton
class CollectionRepositoryImpl @Inject constructor(
    private val collectionDao: CollectionDao,
    private val propertyDao: CollectionPropertyDao,
    private val valueDao: CollectionPropertyValueDao,
    private val viewDao: CollectionViewDao,
    private val pageDao: PageDao,
) : CollectionRepository {

    override fun observeCollections(spaceId: String): Flow<List<Collection>> =
        collectionDao.observeCollectionsForSpace(spaceId).map { rows -> rows.map { it.toModel() } }

    override fun observeCollection(collectionId: String): Flow<Collection?> =
        collectionDao.observeCollection(collectionId).map { it?.toModel() }

    override suspend fun createCollection(spaceId: String, title: String, createdBy: String): AppResult<Collection> = try {
        val now = System.currentTimeMillis()
        val collection = Collection(
            id = UUID.randomUUID().toString(),
            spaceId = spaceId,
            title = title,
            createdBy = createdBy,
            createdAt = now,
            updatedAt = now,
        )
        collectionDao.insert(collection.toEntity())
        // A brand-new collection with zero views is unusable — seed one default Table view.
        viewDao.insert(
            CollectionView(
                id = UUID.randomUUID().toString(),
                collectionId = collection.id,
                name = "Table",
                type = CollectionViewType.TABLE,
                position = POSITION_GAP,
            ).toEntity(),
        )
        AppResult.Success(collection)
    } catch (t: Throwable) {
        AppResult.Failure(AppError.Unknown(t))
    }

    override fun observeProperties(collectionId: String): Flow<List<CollectionProperty>> =
        propertyDao.observePropertiesForCollection(collectionId).map { rows -> rows.map { it.toModel() } }

    override suspend fun createProperty(
        collectionId: String,
        name: String,
        type: CollectionPropertyType,
    ): AppResult<CollectionProperty> = try {
        val siblings = propertyDao.listForCollection(collectionId)
        val position = (siblings.maxOfOrNull { it.position } ?: 0L) + POSITION_GAP
        val property = CollectionProperty(
            id = UUID.randomUUID().toString(),
            collectionId = collectionId,
            name = name,
            type = type,
            position = position,
        )
        propertyDao.insert(property.toEntity())
        AppResult.Success(property)
    } catch (t: Throwable) {
        AppResult.Failure(AppError.Unknown(t))
    }

    override suspend fun renameProperty(propertyId: String, name: String): AppResult<Unit> = try {
        val existing = propertyDao.get(propertyId) ?: return AppResult.Failure(AppError.NotFound)
        propertyDao.update(existing.copy(name = name))
        AppResult.Success(Unit)
    } catch (t: Throwable) {
        AppResult.Failure(AppError.Unknown(t))
    }

    override suspend fun setPropertyVisible(propertyId: String, visible: Boolean): AppResult<Unit> = try {
        val existing = propertyDao.get(propertyId) ?: return AppResult.Failure(AppError.NotFound)
        propertyDao.update(existing.copy(visible = visible))
        AppResult.Success(Unit)
    } catch (t: Throwable) {
        AppResult.Failure(AppError.Unknown(t))
    }

    override suspend fun moveProperty(propertyId: String, direction: MoveDirection): AppResult<Unit> = try {
        val property = propertyDao.get(propertyId) ?: return AppResult.Failure(AppError.NotFound)
        val siblings = propertyDao.listForCollection(property.collectionId)
        val index = siblings.indexOfFirst { it.id == propertyId }
        val swapIndex = if (direction == MoveDirection.UP) index - 1 else index + 1
        if (index == -1 || swapIndex !in siblings.indices) {
            AppResult.Success(Unit)
        } else {
            val current = siblings[index]
            val other = siblings[swapIndex]
            propertyDao.update(current.copy(position = other.position))
            propertyDao.update(other.copy(position = current.position))
            AppResult.Success(Unit)
        }
    } catch (t: Throwable) {
        AppResult.Failure(AppError.Unknown(t))
    }

    override suspend fun deleteProperty(propertyId: String): AppResult<Unit> = try {
        propertyDao.delete(propertyId)
        AppResult.Success(Unit)
    } catch (t: Throwable) {
        AppResult.Failure(AppError.Unknown(t))
    }

    override fun observeOptions(propertyId: String): Flow<List<CollectionPropertyOption>> =
        propertyDao.observeOptionsForProperty(propertyId).map { rows -> rows.map { it.toModel() } }

    override suspend fun addOption(propertyId: String, label: String, colorKey: String): AppResult<CollectionPropertyOption> = try {
        val siblings = propertyDao.listOptionsForProperty(propertyId)
        val position = (siblings.maxOfOrNull { it.position } ?: 0L) + POSITION_GAP
        val option = CollectionPropertyOption(
            id = UUID.randomUUID().toString(),
            propertyId = propertyId,
            label = label,
            colorKey = colorKey,
            position = position,
        )
        propertyDao.insertOption(option.toEntity())
        AppResult.Success(option)
    } catch (t: Throwable) {
        AppResult.Failure(AppError.Unknown(t))
    }

    override suspend fun deleteOption(optionId: String): AppResult<Unit> = try {
        propertyDao.deleteOption(optionId)
        AppResult.Success(Unit)
    } catch (t: Throwable) {
        AppResult.Failure(AppError.Unknown(t))
    }

    override fun observeEntries(collectionId: String): Flow<List<CollectionEntry>> = combine(
        pageDao.observePagesForCollection(collectionId),
        valueDao.observeValuesForCollection(collectionId),
    ) { pages, values ->
        val valuesByPage = values.groupBy { it.pageId }
        pages.map { pageEntity ->
            CollectionEntry(
                page = pageEntity.toModel(),
                values = valuesByPage[pageEntity.id]
                    ?.mapNotNull { row -> row.value?.let { row.propertyId to it } }
                    ?.toMap()
                    ?: emptyMap(),
            )
        }
    }

    override fun observeValuesForPage(pageId: String): Flow<Map<String, String>> =
        valueDao.observeValuesForPage(pageId).map { rows ->
            rows.mapNotNull { row -> row.value?.let { row.propertyId to it } }.toMap()
        }

    override suspend fun setPropertyValue(pageId: String, propertyId: String, value: String?): AppResult<Unit> = try {
        if (value.isNullOrEmpty()) {
            valueDao.delete(pageId, propertyId)
        } else {
            valueDao.upsert(CollectionPropertyValueEntity(pageId = pageId, propertyId = propertyId, value = value))
        }
        AppResult.Success(Unit)
    } catch (t: Throwable) {
        AppResult.Failure(AppError.Unknown(t))
    }

    override fun observeViews(collectionId: String): Flow<List<CollectionView>> =
        viewDao.observeViewsForCollection(collectionId).map { rows -> rows.map { it.toModel() } }

    override suspend fun createView(collectionId: String, name: String, type: CollectionViewType): AppResult<CollectionView> = try {
        val siblings = viewDao.listForCollection(collectionId)
        val position = (siblings.maxOfOrNull { it.position } ?: 0L) + POSITION_GAP
        val view = CollectionView(
            id = UUID.randomUUID().toString(),
            collectionId = collectionId,
            name = name,
            type = type,
            position = position,
        )
        viewDao.insert(view.toEntity())
        AppResult.Success(view)
    } catch (t: Throwable) {
        AppResult.Failure(AppError.Unknown(t))
    }

    override suspend fun updateView(view: CollectionView): AppResult<Unit> = try {
        viewDao.update(view.toEntity())
        AppResult.Success(Unit)
    } catch (t: Throwable) {
        AppResult.Failure(AppError.Unknown(t))
    }

    override suspend fun deleteView(viewId: String): AppResult<Unit> = try {
        viewDao.delete(viewId)
        AppResult.Success(Unit)
    } catch (t: Throwable) {
        AppResult.Failure(AppError.Unknown(t))
    }
}
