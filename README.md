# Super App

A native Android super-app combining a social feed, Notion-style pages and
blocks, tasks/projects, private messaging, community spaces, global search,
notifications, and a lightweight visual canvas — one account, one profile,
one design system, one data layer.

Android only. No Flutter, React Native, KMP, Compose Multiplatform, or
WebView-based UI. Kotlin + Jetpack Compose + native Android APIs.

## Requirements

- JDK 17
- Android SDK with platform 37 and build-tools 36.0.0+ installed
  (`ANDROID_HOME` / `local.properties` → `sdk.dir`)
- Gradle 9.4.1 (via `./gradlew`, see [Known limitations](#known-limitations-of-this-environment))

## Getting started

```bash
cp local.properties.example local.properties
# set sdk.dir to your Android SDK path; leave SUPABASE_* blank to run in
# DataEnvironment.LOCAL_DEMO (fully functional offline, no backend needed)

./gradlew projects       # list all modules
./gradlew assembleDebug  # build the debug APK
./gradlew test           # run unit tests
./gradlew lint           # static analysis
```

## Project layout

```text
app/                    # thin composition root: Application, MainActivity, nav host wiring
core/                    common, model, designsystem, navigation, database,
                         datastore, network, domain, notifications, sync, testing
feature/                 onboarding, auth, home, spaces, editor, create, projects,
                         messages, search, notifications, profile, settings, canvas
benchmark/               macrobenchmark / Baseline Profile module (com.android.test)
build-logic/convention/  Gradle convention plugins (superapp.android.*)
supabase/                SQL migrations + seed data for the optional remote backend
```

See [`ARCHITECTURE.md`](ARCHITECTURE.md) for the module dependency graph and
layering rules, [`PROJECT_STATUS.md`](PROJECT_STATUS.md) for what's done vs.
outstanding, and [`docs/`](docs/) for the per-topic design docs referenced
throughout the codebase.

## Known limitations of this environment

The scaffold in this repository was authored in a sandboxed session with
**no access to `dl.google.com`** (Android SDK Manager / Google's Maven
repository) and no local Android SDK installed, and with `github.com` asset
downloads restricted to the `safetyadrien/superapp` repository only. As a
result:

- No Android/Jetpack Gradle dependency could be resolved or downloaded, so
  `./gradlew assembleDebug` / `test` / `lint` have **not** been run
  successfully end-to-end from this session.
- The Gradle 9.4.1 distribution itself could not be downloaded (it redirects
  through `github.com/gradle/gradle-distributions`, outside this session's
  allowed scope), so the wrapper is configured but unverified here.

Every file was written to be correct against verified current stable release
versions (see [`docs/DEPENDENCIES.md`](docs/DEPENDENCIES.md)), but the first
real build must happen in an environment with normal network access to
`dl.google.com`, `maven.google.com` (or a mirror), and `services.gradle.org`.
See [`PROJECT_STATUS.md`](PROJECT_STATUS.md) for the exact commands run and
their output.
