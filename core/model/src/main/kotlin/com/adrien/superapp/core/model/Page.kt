package com.adrien.superapp.core.model

/**
 * `icon` is a single emoji character. `coverColorKey` names one of a small built-in palette
 * (see `feature:editor`'s cover picker) — a real photo/image cover (`coverUrl`) is a later phase,
 * since it needs an image loader (Coil) this codebase doesn't pull in until a screen actually
 * loads a remote image. `collectionId` is set when this page is a collection entry ("chaque
 * entrée de base de données doit également être une page") — null for an ordinary page.
 */
data class Page(
    val id: String,
    val spaceId: String,
    val parentPageId: String? = null,
    val title: String,
    val icon: String? = null,
    val coverUrl: String? = null,
    val coverColorKey: String? = null,
    val collectionId: String? = null,
    val createdBy: String,
    val createdAt: Long,
    val updatedAt: Long,
    val archivedAt: Long? = null,
)
