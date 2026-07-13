package com.adrien.superapp.core.navigation

import kotlinx.serialization.Serializable

/**
 * Every navigable destination in the app. Kept as `@Serializable` types so
 * Navigation Compose's type-safe routing can (de)serialize them, and so this
 * hierarchy maps cleanly onto Navigation 3's `NavKey` model if/when that
 * library reaches a stable release (see docs/DECISIONS.md).
 *
 * Entity detail routes (task/conversation/canvas) are added as the features
 * that own those entities land — adding them before that would be dead
 * routes with no destination to navigate to. `PostDetail`/`ComposePost`
 * (Phase 2, Post model) and `CreateSpace`/`SpaceDetail`/`PageDetail`
 * (Phase 3, Space/Page models) are the first entity/action routes.
 */
sealed interface AppRoute {
    @Serializable
    data object Home : AppRoute

    @Serializable
    data object Spaces : AppRoute

    @Serializable
    data object Messages : AppRoute

    @Serializable
    data object Profile : AppRoute

    @Serializable
    data object Settings : AppRoute

    @Serializable
    data class PostDetail(val postId: String) : AppRoute

    @Serializable
    data class ComposePost(val replyToPostId: String? = null) : AppRoute

    @Serializable
    data object CreateSpace : AppRoute

    @Serializable
    data class SpaceDetail(val spaceId: String) : AppRoute

    @Serializable
    data class PageDetail(val pageId: String) : AppRoute

    @Serializable
    data class CollectionDetail(val collectionId: String) : AppRoute
}
