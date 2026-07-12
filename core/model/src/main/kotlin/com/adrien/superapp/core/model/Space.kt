package com.adrien.superapp.core.model

enum class SpaceVisibility {
    PRIVATE,
    INVITE_ONLY,
    PUBLIC,
}

enum class SpaceMemberRole {
    OWNER,
    ADMIN,
    EDITOR,
    MEMBER,
    GUEST,
}

data class Space(
    val id: String,
    val name: String,
    val description: String? = null,
    val icon: String? = null,
    val ownerId: String,
    val visibility: SpaceVisibility = SpaceVisibility.PRIVATE,
    val createdAt: Long,
    val updatedAt: Long,
)

data class SpaceMember(
    val spaceId: String,
    val profileId: String,
    val role: SpaceMemberRole,
    val joinedAt: Long,
)

/** A space joined with the counts its browser row needs to render without N+1 queries. */
data class SpaceWithStats(
    val space: Space,
    val memberCount: Int,
    val pageCount: Int,
)
