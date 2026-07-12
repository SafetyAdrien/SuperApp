# Navigation

`core:navigation` owns the route contracts; `:app` owns the actual
`NavHost`/graph wiring.

## Library choice

Using stable `androidx.navigation:navigation-compose` (2.9.8), not
Navigation 3 — see `docs/DECISIONS.md` for why (Navigation 3 has no stable
1.0.0 release yet). Routes are `@Serializable` sealed types, e.g.:

```kotlin
sealed interface AppRoute

@Serializable data object HomeRoute : AppRoute
@Serializable data object SpacesRoute : AppRoute
@Serializable data class PageRoute(val pageId: String) : AppRoute
@Serializable data class ConversationRoute(val conversationId: String) : AppRoute
```

This mirrors Navigation 3's `NavKey` model closely enough that migrating
later is expected to be localized to `core:navigation` and the `:app` nav
host, not a cross-cutting change.

## Deep links

Internal scheme `superapp://`:

```text
superapp://profile/{profileId}
superapp://space/{spaceId}
superapp://page/{pageId}
superapp://post/{postId}
superapp://conversation/{conversationId}
superapp://task/{taskId}
superapp://canvas/{canvasId}
```

An `EntityRef(type, id)` → route resolver (Phase 1) lets any part of the app
open any entity by type + id without knowing which feature module owns the
destination screen — this is how a task can be opened the same way whether
it was reached from a page, a project, the activity feed, search, or a
shared link in a message.

## Android system behavior

Predictive back where supported, standard system back otherwise, saved
navigation state across process death (`rememberSaveable` /
`SavedStateHandle`), edge-to-edge with correct system bar insets.

**Status**: `AppRoute` (Home/Spaces/Messages/Profile/Settings) is
implemented and wired into a real `NavHost` in `:app`
(`navigation/SuperAppNavHost.kt`), driven by the five-destination bottom
navigation bar (`ui/MainScreen.kt`). Deep links (`superapp://...`) and the
`EntityRef` resolver are not implemented yet — there are no entity detail
screens to resolve to before Phase 2.
