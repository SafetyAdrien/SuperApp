# Project status

Last updated: 2026-07-12 (Phase 0 + Phase 1 + Phase 2 + Phase 3 + Phase 4,
same sandboxed session, build still unverified — see "Blocages").

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

## Fonctionnalités en cours

None open mid-implementation — every phase attempted this session reached a
documented stopping point.

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
  page. No page icon/cover picker (the fields exist, always null). No
  archiving/deleting a space or page.
- **Rest of Phase 4**: `IMAGE`, `FILE`, `LINK`, `TASK_REFERENCE`,
  `PAGE_REFERENCE`, `CANVAS_REFERENCE` block types are unbuilt (they need
  attachments/tasks/canvas/cross-page linking, none of which exist yet).
  No block nesting (`Block.parentBlockId` exists but nothing writes or
  reads it — no indentation/nested-list UI). No drag-and-drop reordering —
  only up/down move actions. No changing an existing block's type after
  creation (only choosable at insert time). No `language` picker for
  `CODE` blocks (the field exists, always null). No markdown-style
  shortcuts (typing `# ` to become a heading, `- ` for a bulleted list,
  etc.) — blocks are created only via the type picker.
- **Phases 5–11**: exactly as scoped in the project brief. `core:network`,
  `core:sync`, `core:notifications`, and
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
just `feature:editor`'s. Treat all of it as "reviewed, not verified" until
a real build runs.

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
./gradlew assembleDebug   # first real compile of Phase 0 + 1 + 2 + 3 + 4
./gradlew test            # exercises CreatePostUseCaseTest,
                           # ToggleReactionUseCaseTest, PostDaoTest,
                           # CreateSpaceUseCaseTest, CreatePageUseCaseTest,
                           # SpaceDaoTest, CreateBlockUseCaseTest,
                           # MoveBlockUseCaseTest, BlockDaoTest
./gradlew lint
```

Fix whatever surfaces. Prime suspects, in rough likelihood order: whether
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
at Compose BOM 2026.06.00. Then update this section and `CHANGELOG.md`.

## Dette technique

- KSP version (`2.3.10-2.0.4` in the catalog) is a best-effort guess at the
  patch suffix — confirm against `google/ksp` releases before relying on
  it.
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
option list — not silently broken, just not offered yet.

## Dernier build exécuté

`gradle projects` (system Gradle 8.14.3) on 2026-07-12 — failed at AGP
plugin resolution (network blocker above), not evaluated further. Not
re-attempted after Phase 1, Phase 2, Phase 3, or Phase 4's changes since the
blocker is unchanged; see "What was done instead" above for the static
checks that were run each pass.

## Derniers tests exécutés

None — no test task has run (blocked upstream of test compilation by the
same AGP/SDK unavailability). This session did *write* tests it has not
been able to run: `CreatePostUseCaseTest`, `ToggleReactionUseCaseTest`
(pure JVM, fakes only, core:domain), `PostDaoTest` (Robolectric + in-memory
Room, core:database); from Phase 3, `CreateSpaceUseCaseTest`,
`CreatePageUseCaseTest` (pure JVM, fakes only, core:domain) and
`SpaceDaoTest` (Robolectric + in-memory Room, core:database — covers the
joined member/page-count query, including the "viewer never joined this
space" exclusion case); and from Phase 4, `CreateBlockUseCaseTest` (gap-based
position math: first block, insert-between, append-after-last),
`MoveBlockUseCaseTest` (swap up/down, no-op at either edge — pure JVM,
fakes only, core:domain), and `BlockDaoTest` (Robolectric + in-memory Room,
core:database — ordering by position, delete, content/checked update).
These are the first tests to actually exercise in a networked environment,
since they're the lowest-risk/highest-value ones to confirm first.
