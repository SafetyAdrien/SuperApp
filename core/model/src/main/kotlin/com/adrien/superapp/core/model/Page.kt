package com.adrien.superapp.core.model

/**
 * `icon` is a single emoji character. `coverColorKey` names one of a small built-in palette
 * (see `feature:editor`'s cover picker) — a real photo/image cover (`coverUrl`) is a later phase,
 * since it needs an image loader (Coil) this codebase doesn't pull in until a screen actually
 * loads a remote image.
 */
data class Page(
    val id: String,
    val spaceId: String,
    val parentPageId: String? = null,
    val title: String,
    val icon: String? = null,
    val coverUrl: String? = null,
    val coverColorKey: String? = null,
    val createdBy: String,
    val createdAt: Long,
    val updatedAt: Long,
    val archivedAt: Long? = null,
)
