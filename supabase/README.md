# Supabase backend

This directory holds the SQL migrations and seed data for the optional remote
backend (`DataEnvironment.REMOTE`). It is **not** a separate application — it
only serves data to the SuperApp Android client (see `docs/BACKEND.md`).

## Layout

- `migrations/` — versioned schema migrations, one file per change, applied
  in filename order (`0001_initial_schema.sql`, `0002_...`).
- `seed.sql` — optional local seed data for a self-hosted Supabase instance
  used during development. Never contains production data.

## Status

No migrations have been written yet. Phase 10 (remote backend) introduces:

- `profiles`, `spaces`, `space_members`, `pages`, `blocks`, `posts`,
  `reactions`, `tasks`, `conversations`, `messages`, `notifications`,
  `canvas_documents`, `canvas_nodes` tables mirroring `core/model`.
- Row Level Security policies for every table (no client is trusted for
  authorization decisions; RLS is the enforcement point).
- Supabase Auth for account creation / sign-in.
- Supabase Storage buckets for avatars, banners, and post/message
  attachments.
- Supabase Realtime channels for live sync.

## Secrets

Never commit a service-role key, database password, or JWT signing secret to
this repository. The Android client only ever receives `SUPABASE_URL` and
`SUPABASE_ANON_KEY` (public, RLS-constrained) via `local.properties` — see
`local.properties.example` at the repo root.
