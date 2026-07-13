package com.adrien.superapp.feature.spaces

import com.adrien.superapp.core.model.Collection
import com.adrien.superapp.core.model.CollectionEntry
import com.adrien.superapp.core.model.CollectionProperty
import com.adrien.superapp.core.model.CollectionPropertyOption
import com.adrien.superapp.core.model.CollectionView

data class CollectionDetailUiState(
    val collection: Collection? = null,
    val properties: List<CollectionProperty> = emptyList(),
    val optionsByProperty: Map<String, List<CollectionPropertyOption>> = emptyMap(),
    val views: List<CollectionView> = emptyList(),
    val activeViewId: String? = null,
    /** Entries after the active view's filter/sort has already been applied (`CollectionViewEngine`). */
    val entries: List<CollectionEntry> = emptyList(),
    val isLoading: Boolean = true,
    val isCreatingEntry: Boolean = false,
    val createdEntryPageId: String? = null,
) {
    val activeView: CollectionView?
        get() = views.firstOrNull { it.id == activeViewId } ?: views.firstOrNull()
}
