# Architecture

## Layering

```text
UI (Compose)
  -> ViewModel (StateFlow<UiState>, sealed Action)
    -> Use cases (core:domain)
      -> Repositories (interfaces in core:domain, impl in core:database/network/sync)
        -> Local source of truth (Room, core:database) <-> Remote (core:network)
```

Rules enforced by convention (and to be enforced by lint/review, not yet by
tooling):

- The UI layer never reads network responses or Room entities directly —
  only `UiState` produced by a `ViewModel`.
- `ViewModel`s expose `StateFlow`, take constructor-injected use
  cases/repositories, and hold no `Context` and no Compose types.
- Room is the single local source of truth; a repository's `Flow` comes from
  Room, refreshed by a remote fetch that writes through Room rather than
  being read directly by callers.

## Module graph

```text
:app
 ├─ depends on every :feature:* and every :core:* (composition root only)

:feature:*  (onboarding, auth, home, spaces, editor, create, projects,
             messages, search, notifications, profile, settings, canvas)
 ├─ depends on: core:common, core:model, core:designsystem, core:navigation,
 │              core:domain, core:datastore (via the `superapp.android.feature`
 │              convention plugin — core:datastore is the one exception to
 │              "features go through core:domain": preferences are simple,
 │              reactive settings, not business logic, see docs/DECISIONS.md)
 └─ MUST NOT depend on another :feature:* module directly — cross-feature
    references go through core:navigation (route contracts) or core:model
    (shared entity references), never a direct module dependency.

:core:domain     -> core:common, core:model
:core:database   -> core:common, core:model, core:domain (implements its repository
                    interfaces — see docs/DECISIONS.md; Hilt wires the binding, so
                    :core:database only needs to be on :app's classpath, not on any
                    :feature:*'s)
:core:datastore  -> core:common, core:model
:core:network    -> core:model
:core:sync       -> core:common, core:model, core:database, core:network
:core:notifications -> core:model, core:navigation
:core:navigation -> (no core deps; route contracts only)
:core:designsystem -> (no core deps; pure Compose UI)
:core:testing    -> core:common, core:model, core:domain (fake repositories,
                    consumed as testImplementation — including by core:domain's
                    own tests; this is not a cycle, see the comment in
                    core/domain/build.gradle.kts)
:core:common, :core:model -> no other module (leaf modules)

:benchmark -> targets :app (com.android.test module, not a library)
```

No module may create a circular dependency; `core:*` modules never depend on
`feature:*` or `app`.

## Convention plugins (`build-logic/convention`)

| Plugin id | Applies |
|---|---|
| `superapp.android.application` | AGP application, compileSdk 37 / minSdk 31 / targetSdk 37, Java 17, R8 in release |
| `superapp.android.application.compose` | Compose compiler + BOM-managed Compose deps on an application module |
| `superapp.android.library` | AGP library, same SDK/Java baseline |
| `superapp.android.library.compose` | Compose compiler + BOM-managed Compose deps on a library module |
| `superapp.android.feature` | library + library.compose + hilt + testing + the core module set every feature needs |
| `superapp.android.hilt` | KSP + Hilt Android plugin + hilt-android/-compiler deps |
| `superapp.android.room` | KSP + Room plugin, schema export to `<module>/schemas` |
| `superapp.android.testing` | shared unit + instrumented test dependencies |

All shared dependency versions live in `gradle/libs.versions.toml`; nothing
in a module `build.gradle.kts` hardcodes a version string.

## State and errors

Screens follow `data class XUiState(...)` / `sealed interface XAction` (or an
equivalent typed pair) — see `feature/settings/.../SettingsUiState.kt` and,
since Phase 2, `feature/home/.../ComposePostUiState.kt` /
`PostDetailUiState.kt`, and since Phase 3,
`feature/spaces/.../{SpacesUiState,CreateSpaceUiState,SpaceDetailUiState}.kt`
and `feature/editor/.../PageDetailUiState.kt`. `AppError` (`core:common`) is
the typed error hierarchy; raw exception messages never cross into the UI
layer. Purely static placeholder screens (Messages for now) skip the
ViewModel/UiState pair since they hold no state yet — one is introduced the
moment a screen gets real content, not before.

## Navigation shell (Phase 1) and entity routes (Phase 2+)

`:app` composes the five-destination bottom navigation
(`navigation/TopLevelDestination.kt`), an `AppState` holding the
`NavHostController` (`navigation/AppState.kt`), and the type-safe `NavHost`
(`navigation/SuperAppNavHost.kt`) routing to each feature's screen composable
via the `AppRoute` sealed hierarchy from `core:navigation`. The central
"Créer" destination has no route — `ui/MainScreen.kt` opens
`feature:create`'s `CreateSheetContent` in a `SuperBottomSheet` instead of
navigating; only `CreateAction.NEW_POST` is wired to a real destination
(`AppRoute.ComposePost`) so far, the other seven show a "bientôt disponible"
snackbar.

`AppRoute.PostDetail(postId)` and `AppRoute.ComposePost(replyToPostId?)` are
the first entity/action routes, added in Phase 2 alongside the Post model.
Phase 3 added `AppRoute.CreateSpace`, `AppRoute.SpaceDetail(spaceId)`, and
`AppRoute.PageDetail(pageId)` alongside the Space/Page models — the "Créer"
sheet's `CreateAction.NEW_SPACE` is now wired to `AppRoute.CreateSpace` too,
leaving six of the eight actions on the "bientôt disponible" snackbar.
Screens read their route args via `SavedStateHandle.toRoute<AppRoute.X>()`
(`feature/home/.../PostDetailViewModel.kt`, `ComposePostViewModel.kt`,
`feature/spaces/.../SpaceDetailViewModel.kt`,
`feature/editor/.../PageDetailViewModel.kt`) rather than the NavHost passing
raw string arguments — this is what "type-safe routing" buys: the ViewModel
gets the actual typed `AppRoute` object, not a manually-parsed string.

## Why some directories only contain a placeholder file right now

This repository is being built phase by phase (see `PROJECT_STATUS.md`).
Phase 0 established the module graph, build system, and conventions. Phase 1
added the theme/design tokens, the core `Super*` components, the navigation
shell, `core:datastore`'s real preferences repository, and `core:common`'s
error hierarchy and connectivity observer. Phase 2 added Profile/Post/
Reaction end-to-end: `core:model`, `core:database` (entities, DAOs, the
paginated feed query, demo seeding), `core:domain` (repositories, use
cases), `core:testing` (fake repositories), and `feature:home`'s real feed,
composer, and post-detail screens. Phase 3 added Space/SpaceMember/Page the
same way: `core:model`, `core:database` (entities, DAOs — including the
joined member/page-count query in `SpaceDao`, mappers, demo seeding),
`core:domain` (repositories, use cases), `core:testing` (fake repositories),
`feature:spaces`'s real space browser/creation/detail screens, and
`feature:editor`'s minimal page shell (an editable title and an empty state
— the block editor itself, the reason `feature:editor` exists, is a later
phase).

`core:network`, `core:sync`, `core:notifications`, and the features not
reachable from the bottom nav yet (onboarding, auth, projects, messages'
conversation detail, search, notifications, canvas) still contain only a
placeholder file naming the phase that replaces it.
