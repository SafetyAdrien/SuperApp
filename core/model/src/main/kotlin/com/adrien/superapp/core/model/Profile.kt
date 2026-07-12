package com.adrien.superapp.core.model

/** All timestamps in this module are epoch milliseconds (UTC). */
data class Profile(
    val id: String,
    val handle: String,
    val displayName: String,
    val biography: String? = null,
    val avatarUrl: String? = null,
    val bannerUrl: String? = null,
    val createdAt: Long,
    val updatedAt: Long,
)
