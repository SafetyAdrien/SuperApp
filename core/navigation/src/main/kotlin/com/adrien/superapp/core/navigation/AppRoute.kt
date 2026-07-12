package com.adrien.superapp.core.navigation

import kotlinx.serialization.Serializable

/**
 * Every navigable destination in the app. Kept as `@Serializable` types so
 * Navigation Compose's type-safe routing can (de)serialize them, and so this
 * hierarchy maps cleanly onto Navigation 3's `NavKey` model if/when that
 * library reaches a stable release (see docs/DECISIONS.md).
 *
 * Entity detail routes (page/task/post/conversation/canvas/space) are added
 * as the features that own those entities land, starting Phase 2 — adding
 * them now would be dead routes with no destination to navigate to.
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
}
