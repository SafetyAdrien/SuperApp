package com.adrien.superapp.core.model

/** A Notion-like database: a typed schema (`CollectionProperty`) applied to a set of pages. */
data class Collection(
    val id: String,
    val spaceId: String,
    val title: String,
    val icon: String? = null,
    val createdBy: String,
    val createdAt: Long,
    val updatedAt: Long,
)

data class CollectionProperty(
    val id: String,
    val collectionId: String,
    val name: String,
    val type: CollectionPropertyType,
    val position: Long,
    val visible: Boolean = true,
)

data class CollectionPropertyOption(
    val id: String,
    val propertyId: String,
    val label: String,
    val colorKey: String,
    val position: Long,
)

/**
 * A collection entry, reusing the page/block system per the brief: "chaque entrée de base de
 * données doit également être une page." [values] maps `CollectionProperty.id` to its raw stored
 * value — see docs/DATA_MODEL.md for the per-type encoding (numbers/dates as strings, multi-select
 * as comma-joined option ids, etc.). A property with no row for this entry is simply absent
 * (treated as empty), not stored as an explicit empty value.
 */
data class CollectionEntry(
    val page: Page,
    val values: Map<String, String>,
)

enum class CollectionViewType { TABLE, LIST, KANBAN }

enum class FilterOperator { EQUALS, CONTAINS, IS_CHECKED, IS_NOT_CHECKED, IS_EMPTY, IS_NOT_EMPTY }

/** `groupPropertyId` only applies to [CollectionViewType.KANBAN] — see `CollectionViewEngine`. */
data class CollectionView(
    val id: String,
    val collectionId: String,
    val name: String,
    val type: CollectionViewType,
    val position: Long,
    val sortPropertyId: String? = null,
    val sortDescending: Boolean = false,
    val groupPropertyId: String? = null,
    val filterPropertyId: String? = null,
    val filterOperator: FilterOperator? = null,
    val filterValue: String? = null,
)
