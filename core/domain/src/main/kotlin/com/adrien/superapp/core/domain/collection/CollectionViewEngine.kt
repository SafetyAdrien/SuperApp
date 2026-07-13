package com.adrien.superapp.core.domain.collection

import com.adrien.superapp.core.model.CollectionEntry
import com.adrien.superapp.core.model.CollectionPropertyType
import com.adrien.superapp.core.model.CollectionView
import com.adrien.superapp.core.model.FilterOperator

/**
 * Pure filter/sort/group logic for a [CollectionView] — deliberately independent of Room/Flow so
 * it's cheap to unit test (see `CollectionViewEngineTest`) and reusable across Table/List/Kanban
 * rendering without duplicating the same `when` in three Composables.
 */
object CollectionViewEngine {

    /** Applies a view's filter, then its sort, to a raw entry list. */
    fun apply(entries: List<CollectionEntry>, view: CollectionView): List<CollectionEntry> {
        val filtered = filter(entries, view)
        return sort(filtered, view)
    }

    private fun filter(entries: List<CollectionEntry>, view: CollectionView): List<CollectionEntry> {
        val propertyId = view.filterPropertyId ?: return entries
        val operator = view.filterOperator ?: return entries

        return entries.filter { entry ->
            val value = entry.values[propertyId]
            when (operator) {
                FilterOperator.EQUALS -> value == view.filterValue
                FilterOperator.CONTAINS -> value != null && view.filterValue != null && value.contains(view.filterValue, ignoreCase = true)
                FilterOperator.IS_CHECKED -> value == "true"
                FilterOperator.IS_NOT_CHECKED -> value != "true"
                FilterOperator.IS_EMPTY -> value.isNullOrEmpty()
                FilterOperator.IS_NOT_EMPTY -> !value.isNullOrEmpty()
            }
        }
    }

    private fun sort(entries: List<CollectionEntry>, view: CollectionView): List<CollectionEntry> {
        val propertyId = view.sortPropertyId ?: return entries
        val sorted = entries.sortedBy { entry -> sortKey(entry, propertyId) ?: "" }
        return if (view.sortDescending) sorted.reversed() else sorted
    }

    /** [CollectionPropertyType.CREATED_AT]/`UPDATED_AT` read the page's own field, everything else the stored value. */
    private fun sortKey(entry: CollectionEntry, propertyId: String): String? = when (propertyId) {
        SYSTEM_CREATED_AT -> entry.page.createdAt.toString().padStart(20, '0')
        SYSTEM_UPDATED_AT -> entry.page.updatedAt.toString().padStart(20, '0')
        else -> entry.values[propertyId]?.let { value -> value.toDoubleOrNull()?.let { "%020.6f".format(it) } ?: value }
    }

    /** Groups entries by their value for [CollectionView.groupPropertyId] — Kanban's columns. Null key = "no value". */
    fun group(entries: List<CollectionEntry>, groupPropertyId: String): Map<String?, List<CollectionEntry>> =
        entries.groupBy { entry -> entry.values[groupPropertyId] }

    /** Sentinel propertyId values `sortPropertyId` can hold for the two system timestamp columns. */
    const val SYSTEM_CREATED_AT = "system:createdAt"
    const val SYSTEM_UPDATED_AT = "system:updatedAt"
}
