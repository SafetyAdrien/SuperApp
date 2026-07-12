# Project status

Last updated: 2026-07-12 (Phase 0 + Phase 1 + Phase 2, same sandboxed
session, build still unverified — see "Blocages").

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

## Fonctionnalités en cours

None open mid-implementation — every phase attempted this session reached a
documented stopping point.

## Fonctionnalités restantes

- **Rest of Phase 1**: onboarding (4 screens) and auth (demo/email) screens
  were *not* built — the brief's Phase 1 bullet list doesn't explicitly
  scope them. `feature:onboarding`/`feature:auth` still contain only a
  placeholder file; `MainActivity` launches straight into `MainScreen` with
  no onboarding gate. Known, intentional gap.
- **Rest of Phase 2**: "Abonnements" and "Activité" tabs are empty-state
  only (no follow graph / activity feed model yet — that's really Phase 3+
  territory once Spaces exist). No image attachments on posts. No draft
  persistence for the composer (closing it loses the text). No
  optimistic-publish UI (the brief asks for it; current implementation
  waits for the (synchronous, local-only) write to complete — acceptable
  for now since there's no network latency to hide yet, revisit once
  Phase 10 adds a real backend).
- **Phases 3–11**: exactly as scoped in the project brief. `core:network`,
  `core:sync`, `core:notifications`, and
  `feature:{onboarding,auth,editor,projects,search,notifications,canvas}`
  still contain only a placeholder file — see `ARCHITECTURE.md`
  §"Why some directories only contain a placeholder file right now".
- ~20 `Super*` design-system components not yet built (see
  `docs/DESIGN_SYSTEM.md` §Status) — deferred until a feature needs each
  one.

## Blocages

**Still the important part — unchanged since Phase 0, and now covers
Phase 1 and Phase 2's code too.**

This session's sandboxed network egress blocks two things a real Android
build needs, unchanged across all three phases attempted this session:

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
SavedStateHandle route-arg wiring — meaningfully more surface area for a
real compiler to disagree with than Phase 0/1. Treat all of it as
"reviewed, not verified" until a real build runs.

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
./gradlew assembleDebug   # first real compile of Phase 0 + 1 + 2
./gradlew test            # exercises CreatePostUseCaseTest,
                           # ToggleReactionUseCaseTest, PostDaoTest
./gradlew lint
```

Fix whatever surfaces. Prime suspects, in rough likelihood order: the KSP
version suffix (`docs/DEPENDENCIES.md`), the raw SQL in `PostDao.kt` (column
aliasing for the `@Embedded(prefix = "author_")` join is the most likely
typo location), the `NavDestination.Companion.hasRoute` usage in
`app/.../navigation/AppState.kt`, and whether `ModalBottomSheet` still
needs `@OptIn(ExperimentalMaterial3Api::class)` at Compose BOM 2026.06.00.
Then update this section and `CHANGELOG.md`.

## Dette technique

- KSP version (`2.3.10-2.0.4` in the catalog) is a best-effort guess at the
  patch suffix — confirm against `google/ksp` releases before relying on
  it.
- `activity-compose` pinned to 1.9.0 (1.12.0 was beta-only at query time).
- No launcher icon asset pipeline — placeholder vector shapes, explicitly
  commented as temporary.
- No onboarding gate in `MainActivity` yet.
- `feature:spaces`/`messages`/`profile` are still empty-state screens with
  no ViewModel — intentional (no state to manage yet).
- The feed's Paging `PagingSource` is invalidated wholesale on any write
  (Room's default behavior) — fine at demo-data scale, worth revisiting
  once Phase 10's sync queue can make writes more granular.
- No unit test for `HomeViewModel`/`ComposePostViewModel`/
  `PostDetailViewModel` yet (only the use cases and the DAO are tested) —
  the ViewModels are thin enough that the use-case tests cover the real
  logic, but a `Turbine`-based ViewModel test is reasonable follow-up work.

## TODO justifiés

Every `// Placeholder for Phase N: ...` comment is an intentional, explained
TODO naming the exact phase that replaces it — none are silently-empty
features standing in for real functionality. The "Créer" sheet's seven
non-post actions intentionally only show a snackbar (no create flow exists
yet to open for pages/notes/tasks/projects/messages/spaces/canvas).

## Dernier build exécuté

`gradle projects` (system Gradle 8.14.3) on 2026-07-12 — failed at AGP
plugin resolution (network blocker above), not evaluated further. Not
re-attempted after Phase 1 or Phase 2's changes since the blocker is
unchanged; see "What was done instead" above for the static checks that
were run each pass.

## Derniers tests exécutés

None — no test task has run (blocked upstream of test compilation by the
same AGP/SDK unavailability). This session did *write* tests it has not
been able to run: `CreatePostUseCaseTest`, `ToggleReactionUseCaseTest`
(pure JVM, fakes only, core:domain) and `PostDaoTest` (Robolectric +
in-memory Room, core:database). These are the first tests to actually
exercise in a networked environment, since they're the lowest-risk/
highest-value ones to confirm first.
