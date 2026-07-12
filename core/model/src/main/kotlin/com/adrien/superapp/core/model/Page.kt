package com.adrien.superapp.core.model

/** Block content (the page's actual body) lands in the phase that adds `feature:editor`'s block editor. */
data class Page(
    val id: String,
    val spaceId: String,
    val parentPageId: String? = null,
    val title: String,
    val icon: String? = null,
    val coverUrl: String? = null,
    val createdBy: String,
    val createdAt: Long,
    val updatedAt: Long,
    val archivedAt: Long? = null,
)
