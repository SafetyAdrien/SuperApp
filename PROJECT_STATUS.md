# Project status

Last updated: 2026-07-12 (Phase 0 session).

## Fonctionnalités terminées

- Multi-module Gradle project structure: `:app`, 11 `:core:*` modules, 13
  `:feature:*` modules, `:benchmark`, `build-logic/convention` (all listed
  in `settings.gradle.kts`).
- Version catalog (`gradle/libs.versions.toml`) with every dependency
  version researched against current (2026-07-12) stable releases — see
  `docs/DEPENDENCIES.md` for the confidence level of each entry.
- Eight `superapp.android.*` convention plugins (`application`,
  `application.compose`, `library`, `library.compose`, `feature`, `hilt`,
  `room`, `testing`) enforcing `minSdk 31` / `compileSdk 37` /
  `targetSdk 37`, JDK 17, R8 + resource shrinking in release, KSP (not
  kapt).
- `:app` composition root: `SuperAppApplication` (Hilt), `MainActivity`
  (edge-to-edge, splash screen via `core-splashscreen`, minimal Compose
  content, deep-link intent filter for `superapp://`).
- `local.properties.example` + `core/network` `BuildConfig` wiring for
  `SUPABASE_URL` / `SUPABASE_ANON_KEY` (public values only).
- `supabase/` scaffold (empty `migrations/`, `seed.sql`, `README.md`).
- Full documentation set: `README.md`, `ARCHITECTURE.md`, this file,
  `CHANGELOG.md`, and 12 files under `docs/`.
- `.github/workflows/ci.yml` (lint / test / assembleDebug, Gradle caching).
- Gradle wrapper generated and pinned to 9.4.1
  (`gradle/wrapper/gradle-wrapper.properties`).

## Fonctionnalités en cours

None — Phase 0 (audit & initialization) is the only phase attempted this
session, per the "work phase by phase" rule.

## Fonctionnalités restantes

Everything functional: Phases 1 through 11 exactly as scoped in the
project brief (foundations/theme/design-system, social feed, spaces &
pages, block editor, projects/tasks, messaging, search & notifications,
profile & settings, canvas, remote backend, stabilization). Every
`core:*`/`feature:*` module currently contains exactly one placeholder
Kotlin file naming the phase that replaces it — see `ARCHITECTURE.md`
§"Why some directories only contain a placeholder file right now".

## Blocages

**This is the important part — read before starting Phase 1.**

This session's sandboxed network egress blocks two things a real Android
build needs:

1. **`dl.google.com`** (Android SDK Manager, and the host
   `maven.google.com` itself redirects to for artifact downloads) — every
   `curl` attempt returns `403` at the proxy layer (`gateway answered 403
   to CONNECT`). No local Android SDK is installed either
   (`ANDROID_HOME`/`ANDROID_SDK_ROOT` unset, no `cmdline-tools` found
   anywhere on disk).
2. **GitHub release-asset downloads outside `safetyadrien/superapp`** —
   Gradle's distribution hosting
   (`services.gradle.org/distributions/gradle-9.4.1-bin.zip`) 307-redirects
   to `github.com/gradle/gradle-distributions/releases/...`, which this
   session's GitHub integration rejects with: `"GitHub access to this
   repository is not enabled for this session. Use add_repo to request
   access."` (This is the Claude Code Remote GitHub proxy, not a raw
   network block.)

**Consequence**: no `com.android.*` Gradle plugin, no `androidx.*`/Google
Maven dependency, and no Gradle 9.4.1 distribution could be resolved or
downloaded in this session. `./gradlew assembleDebug`, `./gradlew test`,
and `./gradlew lint` have **not** successfully run.

### Exact commands run and their exact result

```text
$ gradle wrapper --gradle-version 9.4.1 --distribution-type bin
  -> succeeded once com.android.* plugin resolution was avoided during the
     wrapper task itself (root build.gradle.kts temporarily emptied, then
     restored) and `validateDistributionUrl = false` was set (the URL
     pre-check itself hits the same blocked GitHub host). Wrapper files are
     present and correctly point at 9.4.1; see the note below.

$ ./gradlew --version
  -> Downloading https://services.gradle.org/distributions/gradle-9.4.1-bin.zip
     Exception in thread "main" java.io.IOException: Server returned HTTP
     response code: 403 for URL: https://github.com/gradle/gradle-distributions/
     releases/download/v9.4.1/gradle-9.4.1-bin.zip

$ gradle projects   (using the system's pre-installed Gradle 8.14.3, JDK 17
                      installed via apt for this check)
  -> FAILURE: Plugin [id: 'com.android.application', version: '9.2.0',
     apply: false] was not found in any of the following sources: ...
     could not resolve plugin artifact
     'com.android.application:com.android.application.gradle.plugin:9.2.0'
```

Both failures are purely network/environment-scoped — they reproduce on the
very first plugin/distribution resolution, before any of this project's own
Kotlin/Gradle code is evaluated. Nothing in `PROJECT_STATUS.md`,
`ARCHITECTURE.md`, or the module `build.gradle.kts` files has been
exercised by an actual compiler yet.

### What to do next in a networked environment

```bash
./gradlew projects        # should list all 26 modules
./gradlew assembleDebug   # first real compile — expect to need to adjust
                           # docs/DEPENDENCIES.md "Best-effort" entries
                           # (KSP patch version especially)
./gradlew test
./gradlew lint
```

Fix whatever surfaces (most likely: the KSP version suffix, and confirming
the explicit `org.jetbrains.kotlin.android` + Compose-compiler + KSP2
combination behaves as expected under AGP 9's built-in-Kotlin default — see
`docs/DECISIONS.md`), then update this section and `CHANGELOG.md`.

## Dette technique

- KSP version (`2.3.10-2.0.4` in the catalog) is a best-effort guess at the
  patch suffix — confirm against `google/ksp` releases before relying on
  it.
- `activity-compose` pinned to 1.9.0 (last confirmed stable at query time;
  1.12.0 was beta-only) — re-check for a newer stable release.
- No launcher icon asset pipeline — `app/src/main/res/drawable/
  ic_launcher_{background,foreground}.xml` are placeholder vector shapes,
  explicitly commented as temporary, not final brand artwork.
- `docs/PRODUCT.md` §"MVP acceptance" references the brief's full feature
  list rather than duplicating it verbatim — kept the doc from becoming a
  copy of this file.

## TODO justifiés

Every `// Placeholder for Phase N: ...` comment across `core:*`/`feature:*`/
`benchmark` source files is an intentional, explained TODO — each names the
exact phase (from the project brief's phase plan) that replaces it, per the
rule that the only acceptable TODOs are ones requiring a later, explained
step. None of them are silently-empty features standing in for real
functionality; they are module bootstrapping only, and no screen in this
codebase claims to be a finished feature.

## Dernier build exécuté

`gradle projects` (system Gradle 8.14.3) on 2026-07-12 — failed at AGP
plugin resolution (network blocker above), not evaluated further.

## Derniers tests exécutés

None — no test task has run (blocked upstream of test compilation by the
same AGP/SDK unavailability).
