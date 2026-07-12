# Offline-first & sync

The app must be fully usable offline against the local demo dataset. Flow
for any data-backed screen:

```text
Remote API -> RemoteDataSource -> Repository -> Room -> Flow -> ViewModel -> Compose
```

The UI never reads a network response directly; Room is the single local
source of truth.

## DataStore (`core:datastore`)

Preferences (Preferences DataStore, not `SharedPreferences`): theme,
dynamic-color opt-in, language, onboarding-complete flag, notification
prefs, display/text-size prefs, accessibility prefs, selected home tab,
sync settings (e.g. Wi-Fi-only).

## Sync queue (`core:sync`)

A local operation queue drives eventual consistency with the backend:

```kotlin
SyncOperation(
    id, entityType, entityId,
    operationType,   // CREATE, UPDATE, DELETE, REACTION, UPLOAD
    payload, createdAt, retryCount, lastError,
    syncState,       // PENDING, SYNCING, SYNCED, FAILED, CONFLICT
)
```

Driven by WorkManager with exponential backoff, connectivity-aware,
idempotent (safe to retry without duplicating server-side effects),
duplicate-avoiding. A local change is never silently dropped after a failed
sync attempt — it stays `FAILED` (visible to the user) or `CONFLICT`
(surfaced for resolution), never deleted.

## Data environments

```kotlin
enum class DataEnvironment { LOCAL_DEMO, REMOTE }
```

`LOCAL_DEMO` seeds a demo profile, sample spaces/pages/posts/conversations/
tasks/notifications/canvas **only when the local database is empty**, and
supports full create/edit/delete/search/navigate/offline testing with zero
backend configuration. `REMOTE` talks to Supabase (see
`docs/BACKEND.md`) and is opt-in via `local.properties`.

**Status**: not yet implemented — `core/datastore` and `core/sync` each
currently contain only a placeholder file. This is Phase 1 (DataStore) /
Phase 10 (sync queue against a real backend) work; the local demo seed
lands per-feature starting Phase 2.
