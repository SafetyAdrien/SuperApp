# Security

## Secrets

- No secret, token, or service-role key is ever committed. `local.properties`
  is git-ignored; only `local.properties.example` (blank placeholders) is
  tracked.
- The only Supabase values the Android client ever holds are `SUPABASE_URL`
  and `SUPABASE_ANON_KEY` — both public, RLS-constrained values, injected
  into `BuildConfig` from `local.properties` (see `core/network/build.gradle.kts`).
- No service-role key, database password, or JWT signing secret exists
  anywhere in this repository, including `supabase/`.
- Session tokens are stored via Android Keystore-backed encryption
  (`EncryptedSharedPreferences`/`Tink`, wired in `core:datastore` when auth
  lands in Phase 2), never in plain DataStore or SharedPreferences.

## Client trust boundary

- The Android client is never trusted for authorization decisions against
  the remote backend. Every table in Supabase has Row Level Security
  policies; the client only ever sees what RLS allows regardless of what
  the UI displays.
- All network traffic uses HTTPS only.
- Deep links and any URL opened from user content are validated (scheme,
  host allow-list) before being dispatched to an `Intent`.
- Intents are explicit wherever the target component is internal.
  `android:exported` is set explicitly on every manifest component; nothing
  is exported unless it genuinely needs to be (currently: `MainActivity`
  only, for the launcher intent and internal `superapp://` deep links).

## Build

- R8 (`isMinifyEnabled = true`, `isShrinkResources = true`) is enabled for
  release builds via the `superapp.android.application` convention plugin.
- Verbose/debug logging is not compiled into release builds (enforced during
  Phase 1 when the logging wrapper lands in `core:common`).
- Auto-backup explicitly excludes the local Room database and DataStore
  files (`app/src/main/res/xml/backup_rules.xml`,
  `data_extraction_rules.xml`) — restoring a stale local-only database onto
  a new device would resurrect deleted content and desync the sync queue.

## Reporting a concern

This is a personal/demo project; file an issue describing the concern and
avoid including exploit details in a public issue if the project ever gains
real users.
