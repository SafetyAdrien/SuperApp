# Testing strategy

## Layers

- **Unit tests** (`src/test`): ViewModels, use cases, repositories (against
  fakes, not mocks, where reasonable), validators, search ranking, model
  transforms, permission/role logic, sync/conflict resolution, block-editor
  logic, canvas geometry. Fakes and shared test utilities live in
  `core:testing` and are consumed via `testImplementation`.
- **Room tests** (`src/test` + `src/androidTest`): DAOs, relations, indices,
  FTS, migrations, soft deletes, transactions. Schemas are exported to
  `core/database/schemas/` (set via the `superapp.android.room` convention
  plugin) and committed — never `fallbackToDestructiveMigration()` outside
  of a test-only in-memory database.
- **Compose UI tests** (`src/androidTest`): bottom-nav navigation, creating
  a page, adding a block, creating a post, creating a task, sending a local
  message, search, theme switching, empty/error states, offline mode.
- **Integration test** (`src/androidTest`, once Phase 3–7 land): create a
  space → create a page → add blocks → create a linked task → share the
  page into a message → find it again via search.

## Conventions

- Prefer fakes (in-memory implementations of the repository interfaces from
  `core:domain`) over heavy mocking; use MockK only where a fake would be
  disproportionate.
- `kotlinx-coroutines-test` + Turbine for `Flow`/`StateFlow` assertions.
- Truth for assertions.
- Robolectric for JVM-side Android-framework-dependent unit tests that don't
  need a real device/emulator.

## Static analysis

- Android Lint runs with `abortOnError = true` (configured in
  `ConfigureKotlinAndroid.kt`); the CI build fails on any lint error.
- Kotlin formatting/static analysis (ktlint/detekt equivalent) and dependency
  analysis are to be wired in Phase 1 once there is real application code to
  check — added here now would just be scaffolding checking scaffolding.

## What has actually been run so far

Nothing yet — see `PROJECT_STATUS.md` for why (no Android SDK / blocked
`dl.google.com` access in the Phase 0 session) and the exact commands to run
first in a networked environment.
