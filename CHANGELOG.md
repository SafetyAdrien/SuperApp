# Changelog

All notable changes to this project are documented here.
Format loosely follows [Keep a Changelog](https://keepachangelog.com/).

## [Unreleased]

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
