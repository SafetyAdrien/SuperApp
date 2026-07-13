package com.adrien.superapp.core.testing.repository

import com.adrien.superapp.core.common.AppError
import com.adrien.superapp.core.common.AppResult
import com.adrien.superapp.core.domain.repository.CollectionRepository
import com.adrien.superapp.core.domain.repository.MoveDirection
import com.adrien.superapp.core.model.Collection
import com.adrien.superapp.core.model.CollectionEntry
import com.adrien.superapp.core.model.CollectionProperty
import com.adrien.superapp.core.model.CollectionPropertyOption
import com.adrien.superapp.core.model.CollectionPropertyType
import com.adrien.superapp.core.model.CollectionView
import com.adrien.superapp.core.model.CollectionViewType
import com.adrien.superapp.core.model.Page
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.util.UUID

private const val POSITION_GAP = 1000L

class FakeCollectionRepository : CollectionRepository {

    private val collections = MutableStateFlow<List<Collection>>(emptyList())
    private val properties = MutableStateFlow<List<CollectionProperty>>(emptyList())
    private val options = MutableStateFlow<List<CollectionPropertyOption>>(emptyList())
    private val entryPages = MutableStateFlow<List<Page>>(emptyList())
    private val values = MutableStateFlow<Map<Pair<String, String>, String>>(emptyMap())
    private val views = MutableStateFlow<List<CollectionView>>(emptyList())
    private var shouldFail = false

    fun setShouldFail(fail: Boolean) {
        shouldFail = fail
    }

    fun addEntryPage(page: Page) {
        entryPages.value = entryPages.value + page
    }

    override fun observeCollections(spaceId: String) = collections.map { list -> list.filter { it.spaceId == spaceId } }

    override fun observeCollection(collectionId: String) = collections.map { list -> list.firstOrNull { it.id == collectionId } }

    override suspend fun createCollection(spaceId: String, title: String, createdBy: String): AppResult<Collection> {
        if (shouldFail) return AppResult.Failure(AppError.Unknown())

        val now = System.currentTimeMillis()
        val collection = Collection(
            id = UUID.randomUUID().toString(),
            spaceId = spaceId,
            title = title,
            createdBy = createdBy,
            createdAt = now,
            updatedAt = now,
        )
        collections.value = collections.value + collection
        views.value = views.value + CollectionView(
            id = UUID.randomUUID().toString(),
            collectionId = collection.id,
            name = "Table",
            type = CollectionViewType.TABLE,
            position = POSITION_GAP,
        )
        return AppResult.Success(collection)
    }

    override fun observeProperties(collectionId: String) =
        properties.map { list -> list.filter { it.collectionId == collectionId }.sortedBy { it.position } }

    override suspend fun createProperty(
        collectionId: String,
        name: String,
        type: CollectionPropertyType,
    ): AppResult<CollectionProperty> {
        if (shouldFail) return AppResult.Failure(AppError.Unknown())

        val siblings = properties.value.filter { it.collectionId == collectionId }
        val position = (siblings.maxOfOrNull { it.position } ?: 0L) + POSITION_GAP
        val property = CollectionProperty(
            id = UUID.randomUUID().toString(),
            collectionId = collectionId,
            name = name,
            type = type,
            position = position,
        )
        properties.value = properties.value + property
        return AppResult.Success(property)
    }

    override suspend fun renameProperty(propertyId: String, name: String): AppResult<Unit> {
        if (shouldFail) return AppResult.Failure(AppError.Unknown())
        properties.value = properties.value.map { if (it.id == propertyId) it.copy(name = name) else it }
        return AppResult.Success(Unit)
    }

    override suspend fun setPropertyVisible(propertyId: String, visible: Boolean): AppResult<Unit> {
        if (shouldFail) return AppResult.Failure(AppError.Unknown())
        properties.value = properties.value.map { if (it.id == propertyId) it.copy(visible = visible) else it }
        return AppResult.Success(Unit)
    }

    override suspend fun moveProperty(propertyId: String, direction: MoveDirection): AppResult<Unit> {
        if (shouldFail) return AppResult.Failure(AppError.Unknown())

        val property = properties.value.firstOrNull { it.id == propertyId } ?: return AppResult.Failure(AppError.NotFound)
        val siblings = properties.value.filter { it.collectionId == property.collectionId }.sortedBy { it.position }
        val index = siblings.indexOfFirst { it.id == propertyId }
        val swapIndex = if (direction == MoveDirection.UP) index - 1 else index + 1
        if (swapIndex !in siblings.indices) return AppResult.Success(Unit)

        val current = siblings[index]
        val other = siblings[swapIndex]
        properties.value = properties.value.map {
            when (it.id) {
                current.id -> it.copy(position = other.position)
                other.id -> it.copy(position = current.position)
                else -> it
            }
        }
        return AppResult.Success(Unit)
    }

    override suspend fun deleteProperty(propertyId: String): AppResult<Unit> {
        if (shouldFail) return AppResult.Failure(AppError.Unknown())
        properties.value = properties.value.filterNot { it.id == propertyId }
        return AppResult.Success(Unit)
    }

    override fun observeOptions(propertyId: String) =
        options.map { list -> list.filter { it.propertyId == propertyId }.sortedBy { it.position } }

    override suspend fun addOption(propertyId: String, label: String, colorKey: String): AppResult<CollectionPropertyOption> {
        if (shouldFail) return AppResult.Failure(AppError.Unknown())

        val siblings = options.value.filter { it.propertyId == propertyId }
        val position = (siblings.maxOfOrNull { it.position } ?: 0L) + POSITION_GAP
        val option = CollectionPropertyOption(
            id = UUID.randomUUID().toString(),
            propertyId = propertyId,
            label = label,
            colorKey = colorKey,
            position = position,
        )
        options.value = options.value + option
        return AppResult.Success(option)
    }

    override suspend fun deleteOption(optionId: String): AppResult<Unit> {
        if (shouldFail) return AppResult.Failure(AppError.Unknown())
        options.value = options.value.filterNot { it.id == optionId }
        return AppResult.Success(Unit)
    }

    override fun observeEntries(collectionId: String) = entryPages.map { pages ->
        pages.filter { it.collectionId == collectionId }.map { page ->
            CollectionEntry(
                page = page,
                values = values.value.filterKeys { it.first == page.id }.mapKeys { it.key.second },
            )
        }
    }

    override fun observeValuesForPage(pageId: String) =
        values.map { map -> map.filterKeys { it.first == pageId }.mapKeys { it.key.second } }

    override suspend fun setPropertyValue(pageId: String, propertyId: String, value: String?): AppResult<Unit> {
        if (shouldFail) return AppResult.Failure(AppError.Unknown())

        values.value = if (value.isNullOrEmpty()) {
            values.value - (pageId to propertyId)
        } else {
            values.value + ((pageId to propertyId) to value)
        }
        return AppResult.Success(Unit)
    }

    override fun observeViews(collectionId: String) =
        views.map { list -> list.filter { it.collectionId == collectionId }.sortedBy { it.position } }

    override suspend fun createView(collectionId: String, name: String, type: CollectionViewType): AppResult<CollectionView> {
        if (shouldFail) return AppResult.Failure(AppError.Unknown())

        val siblings = views.value.filter { it.collectionId == collectionId }
        val position = (siblings.maxOfOrNull { it.position } ?: 0L) + POSITION_GAP
        val view = CollectionView(
            id = UUID.randomUUID().toString(),
            collectionId = collectionId,
            name = name,
            type = type,
            position = position,
        )
        views.value = views.value + view
        return AppResult.Success(view)
    }

    override suspend fun updateView(view: CollectionView): AppResult<Unit> {
        if (shouldFail) return AppResult.Failure(AppError.Unknown())
        views.value = views.value.map { if (it.id == view.id) view else it }
        return AppResult.Success(Unit)
    }

    override suspend fun deleteView(viewId: String): AppResult<Unit> {
        if (shouldFail) return AppResult.Failure(AppError.Unknown())
        views.value = views.value.filterNot { it.id == viewId }
        return AppResult.Success(Unit)
    }
}
