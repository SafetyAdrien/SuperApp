# Changelog

All notable changes to this project are documented here.
Format loosely follows [Keep a Changelog](https://keepachangelog.com/).

## [Unreleased]

### Fixed — `android.newDsl=false` wasn't actually reaching `build-logic`

A full `./gradlew assembleDebug` still failed with the *entire*
`CommonExtension` DSL error cascade from before, unchanged, even after the
`android.newDsl=false`/`android.builtInKotlin=false` fix and the
`targetSdk` fix below. Root cause: `build-logic` is a separate
composite/included build (`includeBuild("build-logic")`), and Gradle does
not propagate the root project's `gradle.properties` into an included
build — each build only reads its own, and `build-logic` had none. Added
`build-logic/gradle.properties` with the same two properties. See
`docs/DECISIONS.md`.

### Fixed — Library modules don't have `targetSdk`

After the `android.newDsl=false` fix below, `./gradlew
:build-logic:convention:compileKotlin` still failed with one error:
`AndroidLibraryConventionPlugin.kt`'s `defaultConfig { targetSdk = 37 }`.
Not an AGP-9 regression — `LibraryDefaultConfig` never had a `targetSdk`
property (confirmed against AGP 9.2's reference docs: 0 members, vs.
`ApplicationDefaultConfig`'s 8); it's an install-time behavior flag that
only makes sense for an installable app, not a library AAR. This line was
wrong since it was first written and only surfaced now that the module
actually compiles far enough to reach it. Removed.

### Fixed — `build-logic/convention` doesn't compile against AGP 9's DSL

`./gradlew assembleDebug` failed at `:build-logic:convention:compileKotlin`
with `CommonExtension<*, *, *, *, *, *>` ("No type arguments expected") and
a cascade of unresolved `compileSdk`/`defaultConfig`/`minSdk`/
`compileOptions`/`lint`/`buildFeatures`/`compose`/`targetSdk` references.
AGP 9.0 removed `CommonExtension`'s generic type parameters and moved
several DSL block functions onto the concrete `ApplicationExtension`/
`LibraryExtension` types — this repo's convention plugins were written
against the pre-9.0 shape without ever compiling. Set `android.newDsl=false`
and `android.builtInKotlin=false` in `gradle.properties` — Google's own
documented temporary opt-out for this exact class of error (removed in AGP
10.0). See `docs/DECISIONS.md`; migrating `build-logic/convention` to the
new DSL properly is tracked as follow-up work in `PROJECT_STATUS.md`.

### Fixed — KSP version string doesn't resolve

`./gradlew assembleDebug` failed locally with "Plugin
com.google.devtools.ksp version 2.3.10-2.0.4 was not found". That version
was a guess made without network access, following KSP's old
`<kotlin>-<ksp>` combined versioning. Confirmed via Maven Central's real
`maven-metadata.xml` (this session's Bash tool can reach `repo1.maven.org`
even though `dl.google.com` stays blocked) that KSP versions
independently since release 2.3.0 — fixed to the real latest, `ksp =
"2.3.10"`. See `docs/DECISIONS.md`.

### Added — New Phase 4: native databases

Notion-like databases, scoped per the fidelity requirement's replacement
15-phase plan (see `docs/FIDELITY.md`):

- `core:model`: `Collection`, `CollectionProperty`, `CollectionPropertyOption`,
  `CollectionEntry`, `CollectionView`, `CollectionViewType`, `FilterOperator`,
  `CollectionPropertyType` (14 of ~18 property types). `Page.collectionId` —
  a collection entry is a page, per the brief.
- `core:database`: 5 new entities, 4 new DAOs, `CollectionMapper.kt`,
  `PageDao.observePagesForCollection`, `CollectionRepositoryImpl`.
- `core:domain`: `CollectionRepository` (18 methods) + 19 use cases;
  `CollectionViewEngine` (pure filter/sort/group logic).
- `core:testing`: `FakeCollectionRepository`.
- `core:navigation`: `AppRoute.CollectionDetail`.
- `feature:spaces`: collections list + creation on `SpaceDetailScreen`;
  `CollectionDetailScreen` (Table/List/Kanban views via a `TabRow`,
  properties-management sheet, create-view sheet, view-config sheet).
- `feature:editor`: `PropertyValueRow` — a type-driven property editor
  section on `PageDetailScreen`, shown when the page is a collection entry.
- Scope cuts (all documented in `docs/DECISIONS.md`): Gallery/Calendar
  views deferred; `FILE`/`RELATION`/`AGGREGATION`/`FORMULA`/`LAST_EDITED_BY`
  property types deferred; single-condition filter/sort per view, not
  compound; property visibility is global, not per-view; `SELECT`/
  `MULTI_SELECT`/`STATUS` values store option labels, not ids.
- New tests: `CollectionViewEngineTest`, `MovePropertyUseCaseTest`
  (core:domain), `CollectionDaoTest` (core:database).

### Known issue (unchanged from Phase 0–4/retrofit, now also covers this pass)

- Still not compiled end-to-end in this sandbox — see `PROJECT_STATUS.md`
  §Blocages. First uses of `@OptIn(ExperimentalCoroutinesApi::class)`
  (`flatMapLatest`) and the `combine(Iterable<Flow<T>>, ...)` overload for a
  dynamic, runtime-sized list of flows (one per select-like property).

### Added — Fidelity retrofit: Notion-like workspace home and page header

The brief was extended with a standing requirement for strong structural/
interaction fidelity to Notion Mobile, Bluesky, Discord, and Figma (no
asset/branding copying) — see `docs/FIDELITY.md`. Applied immediately to
the already-built screens rather than deferred:

- `core:model`/`core:database`/`core:domain`: `Page.coverColorKey`;
  `PageRepository` gained `observeRecentPages`, `updateIcon`,
  `updateCoverColor`, `deletePage`; `BlockRepository` gained `updateType`.
  New use cases: `ObserveRecentPagesUseCase`, `UpdatePageIconUseCase`,
  `UpdatePageCoverUseCase`, `DeletePageUseCase`, `ChangeBlockTypeUseCase`.
- `core:designsystem`: `SuperAppCoverColors`, a 6-color named palette for
  page covers.
- `feature:spaces`: `SpacesScreen` rebuilt as a collapsible-sections
  workspace home (Récents/Favoris/Espaces/Pages privées/Pages partagées/
  Modèles/Corbeille); `PageListItem` renders a page's emoji icon.
- `feature:editor`: `PageDetailScreen` gained a cover-color band, an icon
  badge, a compact breadcrumb, a page-level "⋮" menu (icon/cover/delete),
  a real "/" command that transforms the current block's type in place,
  and long-press block selection (highlighted state, not drag-and-drop).
- `feature:home`: top bar gained search/notification icon buttons.
- New tests: `DeletePageUseCaseTest`, `ObserveRecentPagesUseCaseTest`,
  `ChangeBlockTypeUseCaseTest` (core:domain), `PageDaoTest` (core:database).

### Known issue (unchanged from Phase 0–4, now also covers the retrofit)

- Still not compiled end-to-end in this sandbox — see `PROJECT_STATUS.md`
  §Blocages. First uses of `combinedClickable`
  (`@OptIn(ExperimentalFoundationApi::class)`) and `animateFloatAsState`
  in this codebase, plus several more unverified extended-icon references.

### Added — Phase 4: block editor

- `core:model`: `BlockType` (11 of the brief's 15 types; 6 deferred — see
  `docs/DATA_MODEL.md`), `Block` (`checked`/`language` as concrete fields
  instead of a generic `properties` map — see `docs/DECISIONS.md`).
- `core:database`: `BlockEntity`, `BlockDao`, mapper, extended
  `SuperAppDatabase`/`DatabaseModule`. `BlockRepositoryImpl` computes each
  new block's position with a gap-based scheme so inserting between two
  blocks never shifts other rows. `DemoDataSeeder` now seeds 3 blocks per
  demo page.
- `core:domain`: `BlockRepository` (+ `MoveDirection`) and six use cases
  (`ObserveBlocksUseCase`, `CreateBlockUseCase`, `UpdateBlockContentUseCase`,
  `ToggleBlockCheckedUseCase`, `DeleteBlockUseCase`, `MoveBlockUseCase`).
- `core:testing`: `FakeBlockRepository` (same gap-based position logic as
  the real repository).
- `core:designsystem`: `SuperTextField` gained an optional `textStyle`
  parameter (default unchanged).
- Convention plugins: `material-icons-extended` moved from
  `core:designsystem`-only to the `superapp.android.feature` convention
  plugin — every feature module gets consistent icon access now, and this
  retroactively de-risks unconfirmed icon usages from Phase 1–3.
- `feature:editor`: `PageDetailScreen` now renders a real, editable block
  list (`BlockRow`, per-type rendering) with a "slash-menu" equivalent
  bottom sheet (`BlockTypePickerContent`) and a per-block action menu
  (insert below / move up / move down / delete).
- New tests: `CreateBlockUseCaseTest`, `MoveBlockUseCaseTest`
  (core:domain), `BlockDaoTest` (core:database, Robolectric + in-memory
  Room).

### Known issue (unchanged from Phase 0–3, now also covers Phase 4's code)

- Still not compiled end-to-end in this sandbox — see `PROJECT_STATUS.md`
  §Blocages. Phase 4 is the riskiest UI code so far: destructuring
  `items()` + a tuple `key` lambda in `PageDetailScreen.kt`, several
  `Modifier.weight()` calls across nested `Row`/`Box` scopes in
  `BlockRow.kt`, and a convention-plugin change
  (`AndroidFeatureConventionPlugin.kt`) that affects every feature
  module's classpath, not just `feature:editor`'s.

### Added — Phase 3: spaces and pages

- `core:model`: `Space`/`SpaceVisibility`, `SpaceMember`/`SpaceMemberRole`,
  `SpaceWithStats`, `Page`.
- `core:database`: `SpaceEntity`/`SpaceMemberEntity`/`PageEntity`,
  `SpaceDao` (joined member/page-count query, same `*WithStatsRow`
  convention as Phase 2's `PostFeedRow`), `PageDao`, mappers.
  `DemoDataSeeder` now also seeds 2 demo spaces (with the demo profile as
  `OWNER`) and 2 pages per space.
- `core:domain`: `SpaceRepository`/`PageRepository` interfaces (implemented
  in `core:database`) and seven use cases (`ObserveSpacesUseCase`,
  `ObserveSpaceUseCase`, `CreateSpaceUseCase`, `ObservePagesUseCase`,
  `ObservePageUseCase`, `CreatePageUseCase`, `RenamePageUseCase`).
- `core:testing`: `FakeSpaceRepository`, `FakePageRepository`.
- `core:navigation`: `AppRoute.CreateSpace`, `AppRoute.SpaceDetail`,
  `AppRoute.PageDetail`.
- `feature:spaces`: real space browser, space creation (name/description/
  visibility), and space detail (top-level pages + "new page" FAB). The
  "Créer" sheet's "Nouvel espace" action now opens the real create-space
  screen.
- `feature:editor`: first real code — a minimal page shell (editable title,
  autosaved; explicit "block editor coming soon" empty state). The actual
  block editor remains unbuilt.
- New tests: `CreateSpaceUseCaseTest`, `CreatePageUseCaseTest`
  (core:domain), `SpaceDaoTest` (core:database, Robolectric + in-memory
  Room).

### Known issue (unchanged from Phase 0/1/2, now also covers Phase 3's code)

- Still not compiled end-to-end in this sandbox — see `PROJECT_STATUS.md`
  §Blocages. Phase 3 repeats Phase 2's joined-query pattern for `SpaceDao`
  and adds a `popUpTo<AppRoute.Spaces>()` reified type-safe navigation call
  that has not been checked against a real compiler.

### Added — Phase 2: social home feed

- `core:model`: `Profile`, `Post`/`PostWithAuthor`, `Reaction`,
  `EntityType`, `PostVisibility`.
- `core:database`: Room entities/DAOs/`SuperAppDatabase` for the above, a
  single joined-and-paginated feed query (`PostDao.pagingSource`), and
  `DemoDataSeeder` (one demo profile + six posts, inserted only when the
  database is empty).
- `core:domain`: `ProfileRepository`/`PostRepository`/`ReactionRepository`
  interfaces (implemented in `core:database`) and six use cases
  (`ObserveFeedUseCase`, `ObservePostUseCase`, `ObserveRepliesUseCase`,
  `CreatePostUseCase`, `ToggleReactionUseCase`,
  `ObserveCurrentProfileUseCase`).
- `core:testing`: fake repositories for the above, shared across modules.
- `core:navigation`: `AppRoute.PostDetail`/`AppRoute.ComposePost` — the
  first entity/action routes.
- `core:designsystem`: `SuperAvatar`, `SuperTextField`.
- `feature:home`: real paginated feed (Paging 3 + Compose), `PostCard`,
  post composer with a character counter, post detail screen with replies,
  reaction toggling. The "Créer" sheet's "Nouvelle publication" action now
  opens the real composer.
- `SuperAppApplication` now seeds demo data on startup via a new
  `@ApplicationScope` `CoroutineScope` (`core:common`).
- First tests: `CreatePostUseCaseTest`, `ToggleReactionUseCaseTest`
  (core:domain), `PostDaoTest` (core:database, Robolectric + in-memory
  Room).

### Known issue (unchanged from Phase 0/1, now also covers Phase 2's code)

- Still not compiled end-to-end in this sandbox — see `PROJECT_STATUS.md`
  §Blocages. Phase 2 introduced the first non-trivial raw SQL and the
  first Paging 3/SavedStateHandle-route wiring, so it carries the highest
  compile-error risk of the three phases attempted so far.

### Added — Phase 1: foundations

- `core:common`: `AppError`/`AppResult` typed-error hierarchy, `ThemeMode`/
  `DataEnvironment` enums, `Dispatcher` Hilt qualifiers, real
  `ConnectivityObserver`.
- `core:datastore`: real `UserPreferencesRepository` (Preferences DataStore)
  covering theme, dynamic color, onboarding flag, selected home tab, data
  environment, Wi-Fi-only sync, reduce-motion; now a dependency of every
  `feature:*` module.
- `core:navigation`: `AppRoute` sealed hierarchy for the five bottom-nav
  destinations.
- `core:designsystem`: full token set + `SuperAppTheme` (system/light/dark,
  Android 12+ dynamic color); ten `Super*` components with previews.
- `:app`: five-destination bottom navigation, type-safe `NavHost`, the
  central "Créer" bottom sheet (`feature:create`), `AppViewModel` driving
  theme + splash-screen keep-on-screen condition. `MainActivity` now
  renders `SuperAppTheme` + the real navigation shell instead of Phase 0's
  static `MaterialTheme` placeholder.
- Real screens: `feature:home`/`spaces`/`messages`/`profile` (empty-state),
  `feature:settings` (fully functional theme + dynamic-color picker backed
  by a real `SettingsViewModel`).
- `ACCESS_NETWORK_STATE` permission added to the manifest.

### Known issue (unchanged from Phase 0, now also covers Phase 1's code)

- Still not compiled end-to-end in this sandbox — see `PROJECT_STATUS.md`
  §Blocages. Phase 1 added substantially more Compose/Hilt/Navigation code
  than Phase 0's mostly-Gradle scaffold, so treat it as reviewed-but-
  unverified until a real `./gradlew assembleDebug` runs.

### Added — Phase 0: audit and initialization

- Multi-module Gradle project scaffold: `:app`, 11 `:core:*` modules, 13
  `:feature:*` modules, `:benchmark`, and `build-logic/convention`.
- Gradle Kotlin DSL build with a central `gradle/libs.versions.toml` version
  catalog and eight `superapp.android.*` convention plugins.
- Gradle wrapper pinned to 9.4.1; `minSdk 31` / `compileSdk 37` /
  `targetSdk 37` set via convention plugins, no `maxSdk`.
- `:app` composition root: `SuperAppApplication` (`@HiltAndroidApp`),
  `MainActivity` (edge-to-edge, splash screen, Compose content root),
  manifest with a `superapp://` deep-link scheme placeholder.
- `local.properties.example` and BuildConfig wiring for `SUPABASE_URL` /
  `SUPABASE_ANON_KEY` (public values only, never committed).
- `supabase/` scaffold (`migrations/`, `seed.sql`, `README.md`).
- Documentation set: `README.md`, `ARCHITECTURE.md`, `PROJECT_STATUS.md`,
  this file, and 12 topic docs under `docs/`.
- `.github/workflows/ci.yml` running `lint`, `test`, `assembleDebug` with
  Gradle caching.

### Known issue

- The build has not been verified end-to-end in this environment: Android
  SDK / Google Maven access (`dl.google.com`) is blocked, and the Gradle
  9.4.1 distribution download is blocked by the session's GitHub access
  scope. See `PROJECT_STATUS.md` for exact commands and errors.
