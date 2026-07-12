# Project status

Last updated: 2026-07-12 (Phase 0 + Phase 1 session — same sandboxed
session, build still unverified, see "Blocages").

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
- **`core:navigation`**: `AppRoute` sealed hierarchy (Home/Spaces/Messages/
  Profile/Settings), `@Serializable`, ready for Navigation Compose's
  type-safe routing.
- **`core:designsystem`**: full token set (`SuperAppColors`/`Typography`/
  `Shapes`/`Spacing`/`Elevation`/`Motion`) + `SuperAppTheme` (system/light/
  dark + Android 12+ dynamic color behind a togglable preference). Ten
  components with light/dark previews: `SuperTopAppBar`,
  `SuperBottomNavigation`/`SuperNavigationItem`, `SuperFloatingActionButton`,
  `SuperIconButton`, `SuperPrimaryButton`, `SuperBottomSheet`,
  `SuperEmptyState`, `SuperDivider`, `SuperLoadingIndicator`,
  `SuperOfflineBanner`.
- **Navigation shell (`:app`)**: `TopLevelDestination` (5 destinations),
  `AppState`/`rememberAppState`, type-safe `SuperAppNavHost`, `MainScreen`
  (bottom nav + offline banner + nav host + "Créer" bottom sheet).
- **`feature:create`**: `CreateAction` (8 shortcuts) + `CreateSheetContent`
  — each action currently dismisses the sheet and shows a "bientôt
  disponible" snackbar (no create flow exists yet to open).
- **Real screens**: `feature:home`, `feature:spaces`, `feature:messages`,
  `feature:profile` (empty-state placeholders using `SuperTopAppBar` +
  `SuperEmptyState` — real composables, no fake data yet).
  `feature:settings` is fully functional: `SettingsViewModel`
  (`SettingsUiState`/`SettingsAction`) reads/writes theme mode and
  dynamic-color through `UserPreferencesRepository`.
- **`:app`**: `AppViewModel` combines preferences + connectivity into
  `AppUiState`; `MainActivity` keeps the splash screen on-screen until the
  first preferences read completes, then renders `SuperAppTheme` +
  `MainScreen` — replacing Phase 0's static `MaterialTheme` placeholder.
  `ACCESS_NETWORK_STATE` (normal permission) added to the manifest for
  `ConnectivityObserver`.

## Fonctionnalités en cours

None open mid-implementation — both phases attempted this session reached a
documented stopping point.

## Fonctionnalités restantes

- **Rest of Phase 1**: onboarding (4 screens) and auth (demo/email) screens
  were *not* built this pass — the brief's Phase 1 bullet list doesn't
  explicitly scope them, and MVP acceptance needs them before Phase 2 makes
  full sense. `feature:onboarding` and `feature:auth` still contain only a
  placeholder file. `MainActivity` currently launches straight into
  `MainScreen` (no onboarding gate) — this is a known, intentional gap, not
  an oversight.
- **Phases 2–11**: exactly as scoped in the project brief (social feed,
  spaces & pages, block editor, projects/tasks, messaging, search &
  notifications, profile & settings content, canvas, remote backend,
  stabilization). `core:database`, `core:domain`, `core:network`,
  `core:sync`, `core:notifications`, `core:testing`, and
  `feature:{onboarding,auth,editor,projects,search,notifications,canvas}`
  still contain only a placeholder file naming the phase that replaces it
  — see `ARCHITECTURE.md` §"Why some directories only contain a placeholder
  file right now".
- ~22 `Super*` design-system components not yet built (see
  `docs/DESIGN_SYSTEM.md` §Status for the exact list) — deferred until a
  feature actually needs each one.

## Blocages

**This is still the important part — unchanged since Phase 0, and it now
also covers Phase 1's code.**

This session's sandboxed network egress blocks two things a real Android
build needs, and neither has changed between the Phase 0 and Phase 1 passes
of this session:

1. **`dl.google.com`** (Android SDK Manager, and the host
   `maven.google.com` itself redirects to for artifact downloads) — every
   `curl` attempt returns `403` at the proxy layer.
2. **GitHub release-asset downloads outside `safetyadrien/superapp`** —
   blocks downloading the Gradle 9.4.1 distribution
   (hosted at `github.com/gradle/gradle-distributions/releases`).

**Consequence**: exactly as in Phase 0 — no `com.android.*` Gradle plugin,
no `androidx.*`/Google Maven dependency, and no Gradle 9.4.1 distribution
could be resolved in this session. **All of Phase 1's Kotlin/Compose code
above has been written carefully and statically checked (see below) but has
never been compiled.** Given the size of what Phase 1 added (theme,
Compose UI, Hilt DI, DataStore, Navigation Compose type-safe routing), the
odds of at least one real compile error are meaningfully higher than for
Phase 0's mostly-boilerplate Gradle files — treat this code as "reviewed,
not verified."

### Exact commands run and their exact result (Phase 0; re-attempting in
this Phase 1 pass would reproduce identically since nothing about the
sandbox's network policy changed)

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
- Cross-checked every `libs.*` accessor in every `build.gradle.kts` against
  `gradle/libs.versions.toml` (all resolve).
- Cross-checked every `libs.findLibrary("...")` string literal in the
  convention plugins against the catalog (all resolve).
- Re-validated every XML resource file with `xmllint --noout` (all
  well-formed) and the version catalog with Python's `tomllib` (valid).
- Manually re-read the trickier Compose/Navigation APIs used
  (`NavDestination.Companion.hasRoute`, `ModalBottomSheet`,
  `dynamicLightColorScheme`/`dynamicDarkColorScheme`, `hiltViewModel()`,
  `collectAsStateWithLifecycle()`) against what's documented as stable for
  the pinned library versions.

None of this substitutes for an actual `./gradlew assembleDebug`.

### What to do next in a networked environment

```bash
./gradlew projects        # should list all 26 modules
./gradlew assembleDebug   # first real compile of Phase 0 + Phase 1
./gradlew test
./gradlew lint
```

Fix whatever surfaces. Prime suspects, in rough likelihood order: the KSP
version suffix (`docs/DEPENDENCIES.md`), the `NavDestination.Companion.hasRoute`
import/usage in `app/.../navigation/AppState.kt`, and whether
`ModalBottomSheet` still needs `@OptIn(ExperimentalMaterial3Api::class)` at
Compose BOM 2026.06.00 (harmless either way, but worth confirming). Then
update this section and `CHANGELOG.md`.

## Dette technique

- KSP version (`2.3.10-2.0.4` in the catalog) is a best-effort guess at the
  patch suffix — confirm against `google/ksp` releases before relying on
  it.
- `activity-compose` pinned to 1.9.0 (1.12.0 was beta-only at query time).
- No launcher icon asset pipeline — placeholder vector shapes, explicitly
  commented as temporary.
- No onboarding gate in `MainActivity` yet (see "Fonctionnalités
  restantes").
- `feature:home`/`spaces`/`messages`/`profile` are empty-state screens with
  no ViewModel — intentional (no state to manage yet), but means they are
  not yet exercising the `UiState`/`Action` pattern `ARCHITECTURE.md`
  mandates; that lands with each feature's real data in Phase 2+.

## TODO justifiés

Every `// Placeholder for Phase N: ...` comment is an intentional, explained
TODO naming the exact phase that replaces it — none are silently-empty
features standing in for real functionality. New this pass: the "Créer"
sheet's actions intentionally only show a snackbar (no create flow exists
yet to open, and faking one would be worse than being honest that it isn't
built).

## Dernier build exécuté

`gradle projects` (system Gradle 8.14.3) on 2026-07-12 — failed at AGP
plugin resolution (network blocker above), not evaluated further. Not
re-attempted after Phase 1's changes since the blocker is unchanged; see
"What was done instead" above for the static checks that were run.

## Derniers tests exécutés

None — no test task has run (blocked upstream of test compilation by the
same AGP/SDK unavailability). No unit tests were added this Phase 1 pass
either (the only genuinely testable unit so far, `SettingsViewModel`, is
small enough that writing its test without being able to run it risked
adding more unverified code; it's next on deck once a real build exists).
