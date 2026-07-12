# Backend (Supabase)

The Android app is fully functional without any backend (`DataEnvironment.
LOCAL_DEMO`, see `docs/OFFLINE_SYNC.md`). Supabase is the optional remote
backend for `DataEnvironment.REMOTE` — it is a data service for this one
client, not a separate application.

## What it provides

- **Auth** — email sign-up/sign-in, session refresh.
- **PostgreSQL** — one table per `core:model` entity (see
  `docs/DATA_MODEL.md`), migrations tracked under `supabase/migrations/`.
- **Storage** — avatars, banners, post/message/page attachments.
- **Realtime** — live sync channels, feeding the same `core:sync` pipeline
  used for offline queuing so the app behaves the same whether a change
  arrived from a local sync retry or a Realtime push.
- **Row Level Security** — the actual authorization boundary; the client is
  never trusted for permission decisions (see `docs/SECURITY.md`).

## Client configuration

Only `SUPABASE_URL` and `SUPABASE_ANON_KEY` (public, RLS-constrained) ever
reach the client, via `local.properties` → `BuildConfig`
(`core/network/build.gradle.kts`). Copy `local.properties.example`, fill
those two fields in, leave them blank to stay on `LOCAL_DEMO`. No
service-role key is ever generated for or shipped to the client.

## Repository

`supabase/` at the repo root:

```text
supabase/
├── migrations/   # one .sql file per schema change, applied in filename order
├── seed.sql      # optional local/dev seed data — never production data
└── README.md
```

**Status**: no migrations exist yet — `supabase/migrations/` is currently
empty. This is Phase 10 work; `core/network` currently contains only a
placeholder file plus the `BuildConfig` wiring for the two public client
values.
