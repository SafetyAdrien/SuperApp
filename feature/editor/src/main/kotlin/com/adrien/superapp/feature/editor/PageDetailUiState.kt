package com.adrien.superapp.feature.editor

import com.adrien.superapp.core.model.Block
import com.adrien.superapp.core.model.CollectionProperty
import com.adrien.superapp.core.model.CollectionPropertyOption

data class PageDetailUiState(
    val title: String = "",
    val icon: String? = null,
    val coverColorKey: String? = null,
    val spaceName: String? = null,
    val blocks: List<Block> = emptyList(),
    val isLoading: Boolean = true,
    val notFound: Boolean = false,
    val deleted: Boolean = false,
    /** Non-null when this page is a collection entry ("chaque entrée de base de données doit également être une page"). */
    val collectionId: String? = null,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,
    val createdBy: String = "",
    val properties: List<CollectionProperty> = emptyList(),
    val optionsByProperty: Map<String, List<CollectionPropertyOption>> = emptyMap(),
    val propertyValues: Map<String, String> = emptyMap(),
    val currentProfileId: String? = null,
)
