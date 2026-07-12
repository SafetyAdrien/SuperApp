package com.adrien.superapp.core.model

/**
 * `checked` only applies to [BlockType.CHECKLIST] and `language` only to [BlockType.CODE] — two
 * concrete fields instead of the brief's generic `properties` blob, since only two types need
 * extra data right now; revisit with a real key-value store if a third type needs one.
 */
data class Block(
    val id: String,
    val pageId: String,
    val parentBlockId: String? = null,
    val type: BlockType = BlockType.PARAGRAPH,
    val position: Long,
    val content: String = "",
    val checked: Boolean = false,
    val language: String? = null,
    val createdAt: Long,
    val updatedAt: Long,
)
