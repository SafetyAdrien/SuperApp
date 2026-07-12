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
:core:database   -> core:model
:core:datastore  -> core:model
:core:network    -> core:model
:core:sync       -> core:common, core:model, core:database, core:network
:core:notifications -> core:model, core:navigation
:core:navigation -> (no core deps; route contracts only)
:core:designsystem -> (no core deps; pure Compose UI)
:core:testing    -> core:model (test fakes/utilities, consumed as testImplementation)
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
equivalent typed pair) — see `feature/settings/.../SettingsUiState.kt` for the
first real example. `AppError` (`core:common`) is the typed error hierarchy;
raw exception messages never cross into the UI layer. Purely static
placeholder screens (Home/Spaces/Messages for now) skip the ViewModel/UiState
pair since they hold no state yet — one is introduced the moment a screen
gets real content, not before.

## Navigation shell (Phase 1)

`:app` composes the five-destination bottom navigation
(`navigation/TopLevelDestination.kt`), an `AppState` holding the
`NavHostController` (`navigation/AppState.kt`), and the type-safe `NavHost`
(`navigation/SuperAppNavHost.kt`) routing to each feature's screen composable
via the `AppRoute` sealed hierarchy from `core:navigation`. The central
"Créer" destination has no route — `ui/MainScreen.kt` opens
`feature:create`'s `CreateSheetContent` in a `SuperBottomSheet` instead of
navigating.

## Why some directories only contain a placeholder file right now

This repository is being built phase by phase (see `PROJECT_STATUS.md`).
Phase 0 established the module graph, build system, and conventions. Phase 1
adds the theme/design tokens, the core `Super*` components, the navigation
shell, `core:datastore`'s real preferences repository, `core:common`'s error
hierarchy and connectivity observer, and five real (if mostly empty) feature
screens. `core:database`, `core:domain`, `core:network`, `core:sync`,
`core:notifications`, `core:testing`, and the features not reachable from the
bottom nav yet (onboarding, auth, editor, projects, messages' conversation
detail, search, notifications, canvas) still contain only a placeholder file
naming the phase that replaces it.
