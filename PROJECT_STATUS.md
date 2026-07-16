# Project status

Last updated: 2026-07-12 (Phase 0 + Phase 1 + Phase 2 + Phase 3 + Phase 4 +
a fidelity retrofit pass, same sandboxed session, build still unverified —
see "Blocages"). The brief was extended after Phase 4 with a standing
cross-phase fidelity requirement (Notion/Bluesky/Discord/Figma structure
and interaction fidelity, no asset/brand copying — see `docs/FIDELITY.md`)
and a renumbered 15-phase plan (0–14) that doesn't map 1:1 onto the phase
history below. Per explicit instruction, the retrofit was applied
immediately to the already-built screens (workspace home, page editor)
rather than deferred; the next phase to pick up is the new plan's Phase 4
("Bases de données" — this repository's own Phase 0–4 history already
covers the new plan's Phase 0–3).

## Fonctionnalités terminées

### Phase 0 — audit et initialisation

- Multi-module Gradle project structure: `:app`, 11 `:core:*` modules, 13
  `:feature:*` modules, `:benchmark`, `build-logic/convention` (all listed
  in `settings.gradle.kts`).
- Version catalog (`gradle/libs.versions.toml`) with every dependency
  version researched against current (2026-07-12) stable releases — see
  `docs/DEPENDENCIES.md` for the confidence level of each entry.
- Eight `superapp.android.*` convention plugins enforcing `minSdk 31` /
  `compileSdk 37` / `targetSdk 37`, JDK 17, R8 + resource shrinking in
  release, KSP (not kapt).
- `local.properties.example` + `core/network` `BuildConfig` wiring for
  `SUPABASE_URL` / `SUPABASE_ANON_KEY` (public values only).
- `supabase/` scaffold, full documentation set, `.github/workflows/ci.yml`,
  Gradle wrapper pinned to 9.4.1.

### Phase 1 — fondations

- **`core:common`**: `AppError`/`AppResult` typed-error hierarchy,
  `ThemeMode`/`DataEnvironment` enums, `Dispatcher` qualifier + Hilt
  `DispatcherModule`, `ConnectivityObserver` (real `ConnectivityManager`
  callback-based implementation) + Hilt binding.
- **`core:datastore`**: `UserPreferences` (theme, dynamic color, onboarding
  flag, selected home tab, data environment, Wi-Fi-only sync, reduce-motion)
  backed by a real Preferences DataStore `UserPreferencesRepository` +
  impl + Hilt module. Added as a dependency of every `feature:*` module
  (see `docs/DECISIONS.md`).
- **`core:navigation`**: `AppRoute` sealed hierarchy, `@Serializable`, ready
  for Navigation Compose's type-safe routing.
- **`core:designsystem`**: full token set (`SuperAppColors`/`Typography`/
  `Shapes`/`Spacing`/`Elevation`/`Motion`) + `SuperAppTheme` (system/light/
  dark + Android 12+ dynamic color behind a togglable preference).
- **Navigation shell (`:app`)**: `TopLevelDestination` (5 destinations),
  `AppState`/`rememberAppState`, type-safe `SuperAppNavHost`, `MainScreen`
  (bottom nav + offline banner + nav host + "Créer" bottom sheet).
- **`feature:create`**: `CreateAction` (8 shortcuts) + `CreateSheetContent`.
- **`feature:settings`**: fully functional — `SettingsViewModel`
  reads/writes theme mode and dynamic-color through
  `UserPreferencesRepository`.
- **`:app`**: `AppViewModel` combines preferences + connectivity into
  `AppUiState`; `MainActivity` keeps the splash screen on-screen until the
  first preferences read completes, then renders `SuperAppTheme` +
  `MainScreen`. `ACCESS_NETWORK_STATE` (normal permission) added to the
  manifest for `ConnectivityObserver`.

### Phase 2 — accueil social

- **`core:model`**: `Profile`, `Post` (+ `PostWithAuthor` read model),
  `Reaction` (polymorphic over `entityType`/`entityId`), `EntityType`,
  `PostVisibility`. Timestamps are epoch-millis `Long` (not
  `kotlin.time.Instant` — avoids relying on an API whose stability at
  Kotlin 2.3.10 couldn't be confirmed in this sandbox).
- **`core:database`**: `ProfileEntity`/`PostEntity`/`ReactionEntity`,
  `ProfileDao`/`PostDao`/`ReactionDao`, `SuperAppDatabase` (Room, schema
  export configured), mappers, `DemoDataSeeder` (seeds one demo profile +
  6 posts only when `profiles` is empty). `PostDao.pagingSource()` is one
  query joining posts + author + correlated reaction-count/reply-count/
  viewer-has-reacted subqueries — see `docs/DATA_MODEL.md`.
- **`core:domain`**: `ProfileRepository`/`PostRepository`/
  `ReactionRepository` interfaces; `ObserveFeedUseCase`,
  `ObservePostUseCase`, `ObserveRepliesUseCase`, `CreatePostUseCase`,
  `ToggleReactionUseCase`, `ObserveCurrentProfileUseCase`. Implementations
  live in `core:database` (`ProfileRepositoryImpl` etc.) and are Hilt-bound
  there — feature modules only ever see the `core:domain` interfaces.
- **`core:testing`**: `FakeProfileRepository`/`FakePostRepository`/
  `FakeReactionRepository`, exposed as `api` so any module's tests can use
  them.
- **`core:navigation`**: `AppRoute.PostDetail(postId)` and
  `AppRoute.ComposePost(replyToPostId?)` — the first entity/action routes.
- **`core:designsystem`**: `SuperAvatar` (initials-only) and
  `SuperTextField` added — both genuinely needed by the feed/composer, not
  built speculatively.
- **`feature:home`**: real paginated feed (Paging 3 + Room `PagingSource`)
  on the "Pour vous" tab ("Abonnements"/"Activité" stay empty-state — no
  follow graph or activity model exists yet), `PostCard`, a working
  composer (`ComposePostScreen`/`ViewModel`, character counter, reply
  support), a post detail screen with replies and reaction toggling. The
  "Créer" sheet's "Nouvelle publication" action now opens the real
  composer; the other seven still show a "bientôt disponible" snackbar.
  `SuperAppApplication.onCreate()` triggers `DemoDataSeeder` on an
  `@ApplicationScope` `CoroutineScope` (new in `core:common`).

### Phase 3 — espaces et pages

- **`core:model`**: `Space` (+ `SpaceVisibility`), `SpaceMember` (+
  `SpaceMemberRole`), `SpaceWithStats` (read model: a space plus its member
  and page counts), `Page`.
- **`core:database`**: `SpaceEntity`/`SpaceMemberEntity`/`PageEntity`,
  `SpaceDao`/`PageDao`, mappers, extended `SuperAppDatabase` (still schema
  version 1 — see the comment on the `@Database` annotation, this has never
  shipped to a real device in this sandbox so there is nothing to migrate
  from). `SpaceDao.observeSpacesForMember`/`observeSpace` join a space with
  correlated member-count/page-count subqueries into `SpaceWithStatsRow`,
  the same shape convention as Phase 2's `PostFeedRow`. `DemoDataSeeder` now
  also seeds 2 demo spaces ("Personnel", "Équipe Design"), the demo profile
  as `OWNER` member of both, and 2 pages per space.
- **`core:domain`**: `SpaceRepository`/`PageRepository` interfaces;
  `ObserveSpacesUseCase`, `ObserveSpaceUseCase`, `CreateSpaceUseCase`,
  `ObservePagesUseCase`, `ObservePageUseCase`, `CreatePageUseCase`,
  `RenamePageUseCase`. Same author-resolution pattern as `CreatePostUseCase`:
  the use case resolves the current profile and passes a caller id
  (`ownerId`/`createdBy`) into the repository, which stays pure persistence.
- **`core:testing`**: `FakeSpaceRepository`, `FakePageRepository`.
- **`core:navigation`**: `AppRoute.CreateSpace`, `AppRoute.SpaceDetail
  (spaceId)`, `AppRoute.PageDetail(pageId)`.
- **`feature:spaces`**: real space browser (`SpacesScreen` — list with
  member/page counts, empty state, FAB), `CreateSpaceScreen` (name,
  optional description, visibility radio group — mirrors
  `SettingsScreen`'s plain-Material3 radio pattern since `SuperSegmentedControl`
  isn't built yet), `SpaceDetailScreen` (top-level pages of a space, FAB
  that creates a page titled "Page sans titre" and navigates straight into
  it — same "new page" UX as most block-editor apps, not a Notion visual
  clone). The "Créer" sheet's "Nouvel espace" action now opens
  `CreateSpaceScreen`; the remaining six actions still show "bientôt
  disponible".
- **`feature:editor`**: first real code in this module —
  `PageDetailScreen`/`ViewModel`/`UiState`: an editable title (auto-saved
  via `RenamePageUseCase` on every change) plus an explicit empty state
  explaining the block editor is a later phase. This is intentionally thin;
  `feature:editor`'s actual scope (paragraph/heading/list/checklist/quote/
  callout/code/references, slash menu) is unbuilt.

### Phase 4 — éditeur de blocs

- **`core:model`**: `BlockType` (11 of the brief's 15 types — see
  `docs/DATA_MODEL.md` for which are deferred and why), `Block`
  (`checked`/`language` as concrete fields instead of the brief's generic
  `properties` blob — see `docs/DECISIONS.md`).
- **`core:database`**: `BlockEntity`, `BlockDao` (`observeBlocksForPage`
  Flow + a one-shot `listForPage` used to compute insert/move positions),
  mapper, extended `SuperAppDatabase`/`DatabaseModule` (still schema version
  1, same reasoning as Phase 3). `BlockRepositoryImpl` computes each new
  block's `position` with a gap-based scheme (`(before + after) / 2` when
  inserting between two blocks, `last + 1000` at the end) and moves a block
  by swapping its position with its neighbor's — see `docs/DECISIONS.md`.
  `DemoDataSeeder` now seeds 3 blocks per demo page (a `HEADING_1` echoing
  the page title, a `PARAGRAPH`, and a `CHECKLIST` item) so the editor has
  something to show immediately.
- **`core:domain`**: `BlockRepository` (+ `MoveDirection` enum) and six use
  cases (`ObserveBlocksUseCase`, `CreateBlockUseCase`,
  `UpdateBlockContentUseCase`, `ToggleBlockCheckedUseCase`,
  `DeleteBlockUseCase`, `MoveBlockUseCase`).
- **`core:testing`**: `FakeBlockRepository`, implementing the identical
  gap-based position logic so `core:domain`'s tests exercise the same
  behavior as the real DB path.
- **`core:designsystem`**: `SuperTextField` gained an optional `textStyle`
  parameter (default unchanged) so block rows can render at each type's
  own scale (headings larger, code monospace, checked items struck
  through) through the same component.
- **Convention plugins**: `material-icons-extended` moved from a
  `core:designsystem`-only `implementation` dependency to the
  `superapp.android.feature` convention plugin, so every `:feature:*`
  module gets consistent icon access — see `docs/DECISIONS.md`. This also
  retroactively de-risks icon usages from Phase 1–3 that were never
  actually confirmed to resolve.
- **`feature:editor`**: `PageDetailScreen` now renders the real block list
  — `BlockRow` (per-type rendering: paragraph/headings/bulleted &
  numbered lists with computed prefixes/auto-numbering/checklist with a
  real `Checkbox`/quote in italics/callout in a tinted `Box`/divider/code
  in monospace) plus a per-block `DropdownMenu` (insert below / move up /
  move down / delete) and a bottom "Ajouter un bloc" row. `BlockTypePickerContent`
  is the "slash menu" equivalent — a `SuperBottomSheet` listing all 11
  implemented types, mirroring `feature:create`'s "Créer" sheet pattern.
  `PageDetailViewModel` reconciles the DB's block list (membership/order/
  type/checked — all set by single atomic actions, safe to sync) while
  preserving each block's locally-typed `content` so a fast typer's later
  keystrokes can't be reverted by a slightly-stale DB echo of an earlier
  one (the same problem the title field already had, solved the same way,
  documented inline in `PageDetailViewModel.kt`).

### Retrofit — fidélité Notion/Bluesky (post-Phase 4, before the new Phase 4)

- **`core:model`/`core:database`/`core:domain`**: `Page.coverColorKey`
  (solid-color cover token); `PageRepository` gained
  `observeRecentPages(limit)`, `updateIcon`, `updateCoverColor`,
  `deletePage`; `BlockRepository` gained `updateType` (the "/" command's
  effect). New use cases: `ObserveRecentPagesUseCase`,
  `UpdatePageIconUseCase`, `UpdatePageCoverUseCase`, `DeletePageUseCase`,
  `ChangeBlockTypeUseCase`. `FakePageRepository`/`FakeBlockRepository`
  updated to match.
- **`core:designsystem`**: `SuperAppCoverColors` (`theme/CoverColors.kt`) —
  a 6-color named palette for page covers.
- **`feature:spaces`**: `SpacesScreen` rebuilt as a Notion-like
  collapsible-sections workspace home — `WorkspaceSectionHeader` (chevron
  rotates on expand/collapse) + `EmptySectionRow` for sections with no
  backing feature yet. Sections: Récents (real, `observeRecentPages`),
  Favoris (placeholder — no favorite flag in the model), Espaces (real,
  unchanged from Phase 3, "+" moved from a FAB into the section header),
  Pages privées / Pages partagées / Modèles / Corbeille (placeholders — no
  personal-page concept, no sharing, no templates, no soft-delete UI).
  `PageListItem` now renders `Page.icon` (emoji) when set.
- **`feature:editor`**: `PageDetailScreen` gained a real header —
  `PageCoverBand` (tap to open a color-swatch picker, `PageCoverPickerContent`)
  overlapped by `PageIconBadge` (tap to open an emoji picker,
  `PageIconPickerContent`), a compact breadcrumb (the top bar now shows the
  owning space's name instead of a static "Page" title), and a page-level
  "⋮" menu (change icon / change cover / delete page — `DeletePageUseCase`,
  navigates back via a `uiState.deleted` flag + `LaunchedEffect`). A real
  "/" command: typing "/" at the end of a block's content opens the same
  type-picker sheet used for inserts, but transforms *that* block's type in
  place (`ChangeBlockTypeUseCase`) instead of inserting a new one. Real
  long-press block selection: long-pressing a block's "⋮" handle
  (`combinedClickable`, not the whole row — that would swallow taps meant
  for the text field's cursor) toggles a highlighted `selected` state;
  moving a selected block is still only the existing up/down actions, not
  drag-and-drop (unchanged gap).
- **`feature:home`**: top bar gained search/notification icon buttons
  (both "bientôt disponible" snackbars — real features land in later
  phases) per the Bluesky-like structure requirement; the feed itself was
  already close to spec from Phase 2.
- **Convention/design-system infra**: none beyond what Phase 4 already
  added (`material-icons-extended` on every feature module already covers
  this pass's new icons).

### Nouvelle Phase 4 — Bases de données (post-retrofit)

Native Notion-like databases, scoped per the "Retrofit immédiat" +
"Nouvelle Phase 4 — Bases de données" decisions made when the fidelity
requirement's 15-phase plan replaced the old one (see the conversation
history / `docs/FIDELITY.md`).

- **`core:model`**: `Collection`, `CollectionProperty`,
  `CollectionPropertyOption`, `CollectionEntry`, `CollectionViewType`,
  `FilterOperator`, `CollectionView` (`Collection.kt`);
  `CollectionPropertyType` (14 of ~18 types — see `docs/DATA_MODEL.md`) +
  `isSystemProperty`/`hasOptions` extension vals
  (`CollectionPropertyType.kt`). `Page` gained `collectionId: String?` — a
  collection entry *is* a page, per the brief (see `docs/DECISIONS.md`).
- **`core:database`**: 5 new entities (`CollectionEntity`,
  `CollectionPropertyEntity`, `CollectionPropertyOptionEntity`,
  `CollectionPropertyValueEntity` — composite `(pageId, propertyId)` key —,
  `CollectionViewEntity`), 4 new DAOs (`CollectionDao`,
  `CollectionPropertyDao` — also owns options —, `CollectionPropertyValueDao`,
  `CollectionViewDao`), `CollectionMapper.kt` (bidirectional, enum fields
  stored as their name string with a `runCatching { valueOf(...) }` fallback
  the same way `BlockType` already does), `PageDao.observePagesForCollection`,
  `CollectionRepositoryImpl` (gap-based positions for properties/options/
  views, same `POSITION_GAP = 1000L` scheme as blocks; `createCollection`
  seeds a default "Table" view so a brand-new database is never
  view-less; `observeEntries` combines the entry-page list with a
  properties-joined value query into `CollectionEntry`). `SuperAppDatabase`
  stays version 1 (same reasoning as every prior phase — never shipped to a
  device in this sandbox).
- **`core:domain`**: `CollectionRepository` (18 methods: collections,
  properties, options, entries/values, views) + `MoveDirection`-based
  property reordering (reusing the enum `BlockRepository` already
  declared). 19 new use cases (`ObserveCollectionsUseCase` …
  `DeleteViewUseCase`). `CollectionViewEngine`
  (`core/domain/collection/CollectionViewEngine.kt`) — a pure,
  Room/Flow-independent object: `apply(entries, view)` filters then sorts,
  `group(entries, groupPropertyId)` buckets for Kanban columns. Numeric
  string values sort numerically (zero-padded `%020.6f` formatting) rather
  than lexically; missing values sort as empty/first.
- **`core:testing`**: `FakeCollectionRepository` — full in-memory
  implementation, same gap-position math as the real repository so
  use-case tests exercise identical logic.
- **`core:navigation`**: `AppRoute.CollectionDetail(collectionId)`.
- **`feature:spaces`**: collections list + creation on `SpaceDetailScreen`
  (a "Bases de données" section alongside "Pages", one FAB → bottom sheet
  choosing "Nouvelle page" / "Nouvelle base de données" —
  `CollectionListItem`); `CollectionDetailScreen` (the main screen —
  `TabRow` of views, a "⋮" menu for Properties/Nouvelle vue, a gear icon
  opening the active view's config sheet, `when(activeView.type)` dispatch
  to `CollectionTableView` (name: value chip row per entry — a real
  desktop grid isn't reproduced on phone width, per `docs/FIDELITY.md`'s
  mobile-adaptation rule), `CollectionListView` (title only), or
  `CollectionKanbanView` (`LazyRow` of columns derived from entries' actual
  grouped values, not a property's full option list — no drag-and-drop
  between columns, moving an entry means opening it and changing the
  property value); `CollectionPropertiesSheetContent` (rename/hide/
  reorder/delete properties, expandable inline option editors for
  SELECT/MULTI_SELECT/STATUS, an "Ajouter une propriété" form with a type
  picker); `CreateViewContent` (name + view-type picker);
  `ViewConfigContent` (sort/filter/(Kanban) group pickers, delete-view —
  every control commits immediately, no separate "Save" step, matching
  the rest of the app's persistence pattern).
- **`feature:editor`**: `PropertyValueRow` — one property editor per
  `CollectionPropertyType`, shown as a section on `PageDetailScreen`
  whenever `page.collectionId != null`: text-like types as text fields,
  `CHECKBOX` as a `Checkbox`, `SELECT`/`STATUS`/`MULTI_SELECT` as dropdown
  pickers (storing labels, not ids — see `docs/DECISIONS.md`), `PERSON` as
  a tap-to-assign-to-me toggle, `CREATED_AT`/`UPDATED_AT`/`CREATED_BY` as
  read-only (reading the page's own fields, never a value row).
  `PageDetailViewModel` gained the properties/options/values pipeline
  (`ObservePropertiesUseCase`/`ObserveOptionsUseCase`/
  `ObserveEntryValuesUseCase`/`ObserveCurrentProfileUseCase`/
  `SetPropertyValueUseCase`) with the same DB-echo-vs-local-typing guard
  used for the title and block content, scoped to the free-text property
  types only (select/checkbox/person types are set atomically, so always
  adopting the DB value for them is safe and picks up remote deletions —
  see the inline comment in `PageDetailViewModel`).
- **Navigation**: `composable<AppRoute.CollectionDetail>` added to
  `SuperAppNavHost.kt`; `SpaceDetailScreen`'s `onCollectionClick` param
  (added when the collections-list work landed) is now actually wired at
  its call site.

## Fonctionnalités en cours

None open mid-implementation — every phase (and this retrofit pass)
attempted this session reached a documented stopping point.

## Fonctionnalités restantes

- **Rest of Phase 1**: onboarding (4 screens) and auth (demo/email) screens
  were *not* built — the brief's Phase 1 bullet list doesn't explicitly
  scope them. `feature:onboarding`/`feature:auth` still contain only a
  placeholder file; `MainActivity` launches straight into `MainScreen` with
  no onboarding gate. Known, intentional gap.
- **Rest of Phase 2**: "Abonnements" and "Activité" tabs are still
  empty-state only — a follow graph and an activity-feed model don't exist
  yet, and Phase 3 didn't add either (Spaces existing doesn't imply a
  follow graph). No image attachments on posts. No draft persistence for
  the composer (closing it loses the text). No optimistic-publish UI (the
  brief asks for it; current implementation waits for the (synchronous,
  local-only) write to complete — acceptable for now since there's no
  network latency to hide yet, revisit once Phase 10 adds a real backend).
- **Rest of Phase 3**: no space settings/member-management screen (a viewer
  can create a space and see its member count, but there is no "invite a
  member" flow — that needs either a second demo profile or real accounts,
  neither of which exist). No nested sub-pages: `Page.parentPageId` exists
  in the model and `PageDao.observePagesForSpace` already filters to
  top-level pages only, but no screen creates or navigates into a child
  page. No archiving/deleting a *space* (deleting a *page* is now real,
  see the retrofit section above).
- **Rest of Phase 4**: `IMAGE`, `FILE`, `LINK`, `TASK_REFERENCE`,
  `PAGE_REFERENCE`, `CANVAS_REFERENCE` block types are unbuilt (they need
  attachments/tasks/canvas/cross-page linking, none of which exist yet).
  No block nesting (`Block.parentBlockId` exists but nothing writes or
  reads it — no indentation/nested-list UI). No drag-and-drop reordering —
  only up/down move actions. No `language` picker for `CODE` blocks (the
  field exists, always null). No markdown-style shortcuts (typing `# ` to
  become a heading, `- ` for a bulleted list, etc.) — only the "/" command
  and the explicit type picker create/transform blocks.
- **Rest of the fidelity retrofit**: no real photo page cover (only the
  6-color solid palette — `Page.coverUrl` stays unused until an image
  loader is justified). No favorites, no "pages privées"/"pages
  partagées" concept (would need a personal-page-outside-a-space model and
  real multi-user sharing, neither of which exist), no templates, no
  soft-delete/trash UI (`Page.archivedAt` exists but `deletePage` is a
  hard delete, not a move-to-trash). Discord-like communities and
  Figma-like canvas are entirely unbuilt — see `docs/FIDELITY.md`.
- **Rest of the new Phase 4 (databases)**: Gallery and Calendar views
  (`CollectionViewType` has no case for either); `FILE`/`RELATION`/
  `AGGREGATION`/`FORMULA`/`LAST_EDITED_BY` property types; compound
  (multi-condition) filter/sort — one sort key and one filter condition per
  view, not several; per-view property visibility — `CollectionProperty.visible`
  is one global flag, not one per view; no drag-and-drop between Kanban
  columns (only editing the entry's property value moves it); no name
  resolution for `CREATED_BY`/`PERSON` (both show/store a raw profile id —
  fine for the single local demo profile, needs a real multi-profile
  lookup once accounts exist); no `deleteCollection` (a collection, once
  created, cannot be deleted — deleting its last entry page doesn't remove
  the schema either); renaming/deleting a `SELECT`/`MULTI_SELECT`/`STATUS`
  option doesn't retroactively update entries that already stored the old
  label (see `docs/DECISIONS.md`). All exactly as scoped when this phase
  started — see `docs/DECISIONS.md`'s scope-cut ADR.
- **Phases 5–14 (new plan) / 5–11 (old plan)**: exactly as scoped in the
  project brief. `core:network`, `core:sync`, `core:notifications`, and
  `feature:{onboarding,auth,projects,search,notifications,canvas}` still
  contain only a placeholder file — see `ARCHITECTURE.md` §"Why some
  directories only contain a placeholder file right now".
- ~20 `Super*` design-system components not yet built (see
  `docs/DESIGN_SYSTEM.md` §Status) — deferred until a feature needs each
  one.

## Blocages

**Still the important part — unchanged since Phase 0, and now covers
Phase 1, Phase 2, Phase 3, and Phase 4's code too.**

This session's sandboxed network egress blocks two things a real Android
build needs, unchanged across all five phases attempted this session:

1. **`dl.google.com`** (Android SDK Manager, and the host
   `maven.google.com` itself redirects to for artifact downloads) — every
   `curl` attempt returns `403` at the proxy layer.
2. **GitHub release-asset downloads outside `safetyadrien/superapp`** —
   blocks downloading the Gradle 9.4.1 distribution
   (hosted at `github.com/gradle/gradle-distributions/releases`).

**Consequence**: no `com.android.*` Gradle plugin, no `androidx.*`/Google
Maven dependency, and no Gradle 9.4.1 distribution could be resolved in
this session. **None of this repository's Kotlin/Compose/Room code has
ever been compiled.** Phase 2 added the first non-trivial SQL (a
multi-subquery `PagingSource` query) and the first Paging 3 + Hilt +
SavedStateHandle route-arg wiring; Phase 3 repeated that pattern for
`SpaceDao`'s joined/correlated-subquery query and added a second
`SavedStateHandle.toRoute<AppRoute.X>()` ViewModel (`SpaceDetailViewModel`,
combining 4 flows) plus a `popUpTo<AppRoute.Spaces>()` reified type-safe
call in `SuperAppNavHost.kt`. Phase 4 adds the riskiest UI code so far:
`PageDetailScreen`'s `LazyColumn` destructures `items(blocks.zip(indices))`
with a tuple key lambda, `BlockRow`'s per-type `when` branches mix `Box`/
`Row` scopes with `Modifier.weight()` (only valid inside the right
Row/Column scope — reviewed carefully but not compiler-checked), and the
`superapp.android.feature` convention plugin itself changed (added
`material-icons-extended` to every feature module — see
`docs/DECISIONS.md`), which affects every feature module's classpath, not
just `feature:editor`'s. The fidelity retrofit pass adds its own share:
`BlockActionsMenu`'s `combinedClickable` needs `@OptIn(ExperimentalFoundationApi::class)`
(first use of that experimental API in this codebase);
`WorkspaceSectionHeader`'s `animateFloatAsState` is the first
`androidx.compose.animation.core` usage outside `SuperOfflineBanner` (which
uses the neighboring `AnimatedVisibility`, so the artifact is already on
the classpath, but this specific API wasn't exercised before); several new
icons (`Icons.Filled.InsertEmoticon`, `Image`, `Search`, `Notifications`)
are unverified against the extended icon set the same way Phase 1–4's
icons were. Treat all of it as "reviewed, not verified" until a real build
runs.

The new Phase 4 (databases) adds its own share of unverified surface:
`CollectionDetailViewModel` and `PageDetailViewModel` are the first
ViewModels to `flatMapLatest` a dynamic, runtime-sized list of flows —
`combine(optionProperties.map { observeOptionsUseCase(it.id) }, ...)` uses
the `combine(flows: Iterable<Flow<T>>, transform: (Array<T>) -> R)` overload
for the first time in this codebase (`@OptIn(ExperimentalCoroutinesApi::class)`
for `flatMapLatest` itself is also a first). Both branches of every
`if/else` feeding that pipeline were given matching explicit generic type
arguments (`emptyMap<String, List<CollectionPropertyOption>>()`) rather than
relying on if-expression type-unification inference, specifically because
an earlier draft of `CollectionDetailViewModel` was caught missing that
during this pass's self-review — see "What was done instead" below.
`CollectionDetailScreen`/`CollectionPropertiesSheetContent`/
`ViewConfigContent`/`PropertyValueRow` all use `Modifier.weight(1f)` inside
`Row`s the same risky way `BlockRow` did in Phase 4 (valid only inside the
right scope, not compiler-checked) — two of those files were initially
missing the `androidx.compose.foundation.layout.weight` import entirely and
were caught and fixed in this same self-review pass, which is exactly the
class of error this sandbox's lack of a compiler cannot catch on its own.

### Exact commands run and their exact result (from the Phase 0 pass;
reproduces identically today — the sandbox's network policy hasn't
changed)

```text
$ gradle wrapper --gradle-version 9.4.1 --distribution-type bin
  -> succeeded once com.android.* plugin resolution was avoided during the
     wrapper task itself, with `validateDistributionUrl = false` set.

$ ./gradlew --version
  -> Downloading https://services.gradle.org/distributions/gradle-9.4.1-bin.zip
     Exception in thread "main" java.io.IOException: Server returned HTTP
     response code: 403 for URL: https://github.com/gradle/gradle-distributions/
     releases/download/v9.4.1/gradle-9.4.1-bin.zip

$ gradle projects   (system Gradle 8.14.3, JDK 17 installed via apt)
  -> FAILURE: Plugin [id: 'com.android.application', version: '9.2.0',
     apply: false] was not found in any of the following sources: ...
```

### What was done instead, this pass, to reduce risk without a compiler

- Brace-balance check across every new/modified `.kt` file (none flagged).
- Cross-checked every `libs.*` accessor and every `libs.findLibrary("...")`
  string literal against `gradle/libs.versions.toml` (all resolve).
- Cross-checked every `project(":...")` reference against
  `settings.gradle.kts`'s `include(...)` list (all resolve).
- Re-validated every XML resource file with `xmllint --noout` and the
  version catalog with Python's `tomllib` (both valid).
- Manually re-read the trickier APIs used this pass: the raw `@Query` SQL
  in `PostDao.kt` (correlated subqueries + `@Embedded(prefix=...)` column
  aliasing), `Pager`/`PagingConfig`/`PagingData.map`,
  `SavedStateHandle.toRoute<AppRoute.X>()`, `PagingData.from(...)` in the
  test fakes, and the core:domain-tests-depend-on-core:testing-which-
  depends-on-core:domain configuration graph (not a real Gradle cycle —
  see the comment in `core/domain/build.gradle.kts`).

None of this substitutes for an actual `./gradlew assembleDebug`.

### What to do next in a networked environment

```bash
./gradlew projects        # should list all 26 modules
./gradlew assembleDebug   # first real compile of Phase 0-4 + the retrofit
./gradlew test            # exercises CreatePostUseCaseTest,
                           # ToggleReactionUseCaseTest, PostDaoTest,
                           # CreateSpaceUseCaseTest, CreatePageUseCaseTest,
                           # SpaceDaoTest, CreateBlockUseCaseTest,
                           # MoveBlockUseCaseTest, BlockDaoTest,
                           # DeletePageUseCaseTest, ObserveRecentPagesUseCaseTest,
                           # ChangeBlockTypeUseCaseTest, PageDaoTest,
                           # CollectionViewEngineTest, MovePropertyUseCaseTest,
                           # CollectionDaoTest
./gradlew lint
```

Fix whatever surfaces. From the new Phase 4 (databases): whether the
`combine(Iterable<Flow<T>>, transform: (Array<T>) -> R)` overload used in
`CollectionDetailViewModel.optionsByProperty` and
`PageDetailViewModel`'s properties/options pipeline actually infers the way
reviewed (an empty-`Iterable` edge case — zero option-bearing properties —
falls back to a plain `flowOf(...)` branch instead, so `combine` itself is
never called with zero flows, which is the one input it explicitly
disallows at runtime); whether `TabRow`'s `selectedTabIndex` staying in
sync with `Tab`'s `selected` booleans (computed independently, from
`indexOfFirst` vs. an `it.id == activeView?.id` check) ever drifts;
whether `CollectionPropertyDao`'s two-tables-one-`@Dao` shape (properties +
options) compiles cleanly at this Room version the same way `BlockDao`'s
single-table shape already does. Prime suspects from earlier phases, in
rough likelihood order: whether
every icon used since Phase 1 (`StickyNote2`, `Draw`, `Groups`,
`AutoMirrored.Article`/`Message`, Phase 4's block-type icons) actually needed
the `material-icons-extended` fix or was already in `material-icons-core`
(harmless either way, but confirms whether the fix was load-bearing), the
KSP version suffix (`docs/DEPENDENCIES.md`), the raw SQL in `PostDao.kt`/
`SpaceDao.kt` (column aliasing for `@Embedded(prefix = "author_")`, and
whether the multi-line `SPACE_WITH_STATS_SELECT` string concatenation
produces valid SQL once Room actually parses it), the
`NavDestination.Companion.hasRoute` usage in `app/.../navigation/AppState.kt`,
the `popUpTo<AppRoute.Spaces>()` reified call in `SuperAppNavHost.kt`
(unverified this is the correct type-safe `popUpTo` overload at Navigation
Compose 2.9.8), the destructuring `items(uiState.blocks.zip(numberedIndices))`
+ tuple `key` lambda in `PageDetailScreen.kt`, the `Modifier.weight()` calls
inside `BlockRow.kt`'s nested `Row`/`Box` branches (each must resolve
against its own enclosing scope, not an outer one), and whether
`ModalBottomSheet`/`DropdownMenu` still need `@OptIn(ExperimentalMaterial3Api::class)`
at Compose BOM 2026.06.00. From the retrofit pass: whether
`combinedClickable`'s signature (`onClick`/`onLongClick`/`onClickLabel`/
`onLongClickLabel`) still matches at this Compose BOM, whether
`DropdownMenu` positions correctly as a sibling (not a wrapping `Box`) of
its anchor `IconButton` in `PageDetailScreen`'s top-bar `actions` slot
(same sibling-adjacency pattern already used in `BlockRow.kt`'s
`BlockActionsMenu`, unverified there too), and whether `LazyVerticalGrid`
(used in the new icon/cover pickers) needs any import beyond
`androidx.compose.foundation.lazy.grid.*`. Then update this section and
`CHANGELOG.md`.

## Dette technique

- **`build-logic/convention` targets AGP 9's pre-9.0 DSL shape, opted out via
  `android.newDsl=false`/`android.builtInKotlin=false` (2026-07-13, temporary
  — removed in AGP 10.0)**: after the KSP fix, the next local
  `./gradlew assembleDebug` failed at `:build-logic:convention:compileKotlin`
  with `CommonExtension<*, *, *, *, *, *>` — "No type arguments expected" —
  plus a cascade of unresolved `compileSdk`/`defaultConfig`/`minSdk`/
  `compileOptions`/`lint`/`buildFeatures`/`compose`/`targetSdk` references.
  AGP 9.0 removed `CommonExtension`'s 6 generic type parameters and moved
  several block-lambda DSL functions off it onto the concrete
  `ApplicationExtension`/`LibraryExtension` types. Confirmed via
  `developer.android.com`'s AGP 9.0 release notes and JetBrains'
  `kotlin-agent-skills` AGP-9-migration skill (both reachable this session)
  that `android.newDsl=false` is Google's own documented temporary opt-out
  for exactly this — see `docs/DECISIONS.md`. **Real follow-up work**: once
  a networked environment can actually compile against AGP 9.2's DSL,
  rewrite `build-logic/convention/src/main/kotlin/{ConfigureKotlinAndroid,
  ConfigureAndroidCompose,AndroidLibraryConventionPlugin,...}.kt` against the
  new (non-generic) `CommonExtension` and drop both properties — must happen
  before any future AGP 10.0 upgrade, since the opt-out won't exist anymore.
  **Update**: confirmed working — the opt-out cleared every AGP-9-DSL-shaped
  error; the one error left after it (`AndroidLibraryConventionPlugin.kt`'s
  `targetSdk`) turned out to be unrelated, see next bullet.
- ~~`AndroidLibraryConventionPlugin.kt` set `targetSdk` on a library
  module~~ **Fixed 2026-07-13**: `./gradlew
  :build-logic:convention:compileKotlin` still failed after the
  `android.newDsl=false` fix above, with exactly one error:
  `defaultConfig { targetSdk = 37 }` unresolved. Not an AGP-9 DSL issue —
  confirmed via AGP 9.2's `LibraryDefaultConfig` reference page that
  library modules never had a `targetSdk` property at all (0 members vs.
  `ApplicationDefaultConfig`'s 8) — `targetSdk` is an install-time behavior
  flag meaningless for an AAR. This line was wrong since it was first
  written (a copy-paste from `AndroidApplicationConventionPlugin.kt` that
  was never caught because nothing had compiled yet). Removed.
- ~~KSP version guess~~ **Fixed 2026-07-13**: the user's local
  `./gradlew assembleDebug` failed with "Plugin ... 2.3.10-2.0.4 ... was
  not found" — this session's Bash tool turned out to have real network
  access to Maven Central (`repo1.maven.org`, unlike `dl.google.com` and
  GitHub release-asset downloads, both still blocked — see "Blocages"
  above), so the actual published version list was fetched directly from
  `maven-metadata.xml` instead of guessed. KSP dropped its old
  `<kotlin>-<ksp>` combined version string as of release 2.3.0 and now
  versions independently; the catalog now pins the real latest, `ksp =
  "2.3.10"`. See `docs/DECISIONS.md` and `docs/DEPENDENCIES.md`. This is
  the first version in this catalog confirmed against the actual artifact
  repository rather than release notes/search results — worth doing for
  the rest of the catalog's "Best-effort" rows next time this comes up,
  though `dl.google.com` being blocked still means every `androidx.*`
  artifact (Compose, Room, Navigation, Lifecycle, DataStore, WorkManager,
  Paging, Hilt-navigation-compose, core-ktx, activity-compose, etc.) and
  AGP itself (no longer published to Maven Central beyond ancient 2.x
  releases) remain unverifiable from here.
- `activity-compose` pinned to 1.9.0 (1.12.0 was beta-only at query time).
- No launcher icon asset pipeline — placeholder vector shapes, explicitly
  commented as temporary.
- No onboarding gate in `MainActivity` yet.
- `feature:messages`/`profile` are still empty-state screens with no
  ViewModel — intentional (no state to manage yet). `feature:spaces` lost
  this exemption in Phase 3 (it now has three real ViewModels).
- The feed's Paging `PagingSource` is invalidated wholesale on any write
  (Room's default behavior) — fine at demo-data scale, worth revisiting
  once Phase 10's sync queue can make writes more granular. `SpaceDao`'s
  joined query has the same property.
- No unit test for `HomeViewModel`/`ComposePostViewModel`/
  `PostDetailViewModel`/`SpacesViewModel`/`CreateSpaceViewModel`/
  `SpaceDetailViewModel`/`PageDetailViewModel` yet (only the use cases and
  the DAOs are tested) — the ViewModels are thin enough that the use-case
  tests cover the real logic, but a `Turbine`-based ViewModel test is
  reasonable follow-up work. `PageDetailViewModel`'s block-reconciliation
  logic (preserving locally-typed content across DB echoes) is exactly the
  kind of thing that deserves one first.
- `PageDetailViewModel` auto-saves the title and every block's content on
  every keystroke (no debounce) — acceptable at local-only, single-user
  demo-data scale, but would need debouncing before a real network-backed
  save-on-type exists (Phase 10).
- Block position swaps (`moveBlock`) and inserts (`createBlock`) are not
  transactional — two sequential `blockDao.update()` calls, or a
  `listForPage()` read followed by an `insert()`, could theoretically
  interleave with another write in between. Not a real risk yet (no
  concurrent writers in a local single-user demo), but worth a Room
  `@Transaction` before Phase 10 adds a sync queue that writes
  concurrently.
- No drag-and-drop block reordering (only up/down move actions) — see
  "Fonctionnalités restantes" above.
- No unit test for `SpacesViewModel`'s new `combine` (recent pages +
  spaces) or `PageDetailViewModel`'s icon/cover/delete flows yet — same
  "ViewModels are thin, use-case tests cover the logic" reasoning as
  above, but noted separately since this retrofit added real ViewModel
  logic (the `combine`, the `deleted`-flag navigation trigger) that a
  Turbine test would catch faster than a manual review.
- `deletePage` is a hard delete (cascades to blocks via the existing FK),
  not a move-to-trash — `Page.archivedAt` exists on the model but nothing
  sets it. The "Corbeille" section in the workspace home is therefore a
  placeholder, not a real trash view, even though the underlying column
  exists.
- `CollectionRepositoryImpl.moveProperty` (and the property/view/option
  create-then-seed-a-default-view sequence in `createCollection`) has the
  same non-transactional multiple-sequential-writes shape as
  `BlockRepositoryImpl.moveBlock` — same "not a real risk without
  concurrent writers yet, worth a Room `@Transaction` later" note.
- `ViewConfigContent`'s filter-value `SuperTextField` commits on every
  keystroke via `onUpdate`, same no-debounce tradeoff as the page title/
  block content.
- No unit test for `CollectionDetailViewModel` or `PageDetailViewModel`'s
  new properties/options/values `combine`/`flatMapLatest` pipeline yet —
  same "ViewModels are thin, use-case tests cover the logic" reasoning as
  above, but this is the most complex flow-composition code in the app so
  far (dynamic per-property option flows via `combine(Iterable<Flow<T>>)`)
  and would benefit from a Turbine test more than most.
- `PageDetailViewModel`'s free-text property value merge (guarding against
  the DB-echo-vs-local-typing race the same way block content does) has a
  known minor gap: if a text-like property value is cleared locally while
  a stale DB emission with the old value is still in flight, that stale
  value can briefly reappear before the DB's own delete confirmation
  arrives — see the inline comment and `docs/DECISIONS.md`'s pattern
  discussion. Cosmetic, single-user-local scale, not fixed given the
  complexity/benefit tradeoff.

## TODO justifiés

Every `// Placeholder for Phase N: ...` comment is an intentional, explained
TODO naming the exact phase that replaces it — none are silently-empty
features standing in for real functionality. The "Créer" sheet's six
remaining non-post, non-space actions intentionally only show a snackbar
(no create flow exists yet for notes/tasks/projects/messages/canvas, and
"Nouvelle page" specifically requires picking a space first, which the
sheet doesn't have a flow for yet — creating a page today only happens from
inside `SpaceDetailScreen`). The six deferred `BlockType` values
(`IMAGE`/`FILE`/`LINK`/`TASK_REFERENCE`/`PAGE_REFERENCE`/
`CANVAS_REFERENCE`) simply don't appear in `BlockTypePickerContent`'s
option list — not silently broken, just not offered yet. The workspace
home's Favoris/Pages privées/Pages partagées/Modèles/Corbeille sections
render an explicit "Bientôt disponible" row rather than an empty list —
distinguishing "nothing here yet" (real feature, no data) from "this isn't
built" (`docs/FIDELITY.md`'s "no silent placeholders" rule).

## Dernier build exécuté

`gradle projects` (system Gradle 8.14.3) on 2026-07-12 — failed at AGP
plugin resolution (network blocker above), not evaluated further. Not
re-attempted after Phase 1, Phase 2, Phase 3, Phase 4, the retrofit pass,
or the new Phase 4 (databases)'s changes since the blocker is unchanged;
see "What was done instead" above for the static checks that were run each
pass.

## Derniers tests exécutés

None — no test task has run (blocked upstream of test compilation by the
same AGP/SDK unavailability). This session did *write* tests it has not
been able to run: `CreatePostUseCaseTest`, `ToggleReactionUseCaseTest`
(pure JVM, fakes only, core:domain), `PostDaoTest` (Robolectric + in-memory
Room, core:database); from Phase 3, `CreateSpaceUseCaseTest`,
`CreatePageUseCaseTest` (pure JVM, fakes only, core:domain) and
`SpaceDaoTest` (Robolectric + in-memory Room, core:database — covers the
joined member/page-count query, including the "viewer never joined this
space" exclusion case); from Phase 4, `CreateBlockUseCaseTest` (gap-based
position math: first block, insert-between, append-after-last),
`MoveBlockUseCaseTest` (swap up/down, no-op at either edge — pure JVM,
fakes only, core:domain), and `BlockDaoTest` (Robolectric + in-memory Room,
core:database — ordering by position, delete, content/checked update);
from the retrofit pass, `DeletePageUseCaseTest`, `ObserveRecentPagesUseCaseTest`,
`ChangeBlockTypeUseCaseTest` (pure JVM, fakes only, core:domain), and
`PageDaoTest` (Robolectric + in-memory Room, core:database — recent-pages
ordering, the "viewer never joined" exclusion case, delete); and from the
new Phase 4 (databases), `CollectionViewEngineTest` (pure JVM, no fakes
needed — the engine has no dependencies: EQUALS/CONTAINS/IS_CHECKED/
IS_EMPTY filters, numeric-vs-lexical sort ordering including the
missing-value-sorts-first case, ascending/descending, and grouping
including the null/"no value" bucket), `MovePropertyUseCaseTest` (same
swap-up/down/no-op-at-edges shape as `MoveBlockUseCaseTest`, pure JVM,
fakes only, core:domain), and `CollectionDaoTest` (Robolectric + in-memory
Room, core:database — `CollectionDao`'s per-space scoping and the new
`PageDao.observePagesForCollection` query). These are the first tests to
actually exercise in a networked environment, since they're the
lowest-risk/highest-value ones to confirm first.
