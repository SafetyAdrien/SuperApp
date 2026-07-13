# Decisions

Short ADRs. Newest first.

---

## Solid-color page covers now, real photo covers later

**Contexte** — The fidelity requirement (`docs/FIDELITY.md`) asks for
Notion-like page covers ("ajoutée ou supprimée"). A real photo cover needs
picking an image (Storage Access Framework / Android photo picker) and
loading it (Coil), neither of which any screen has needed yet — this
codebase deliberately hasn't pulled in an image-loading dependency before
now (see `SuperAvatar`'s doc comment, `docs/DESIGN_SYSTEM.md`).

**Décision** — Add `Page.coverColorKey`, a key into a small fixed palette
(`core:designsystem`'s `SuperAppCoverColors`), picked from a bottom sheet
grid. `Page.coverUrl` (already in the model since Phase 3) stays reserved
for a real image cover.

**Raisons** — Ships the actual user-facing behavior (a page can have a
visually distinct cover) without a new dependency, a permission
(`READ_MEDIA_IMAGES`), or unverifiable image-loading code in a sandbox
that cannot compile-test it. Matches Notion's own onboarding UX, which
also offers solid/gradient covers before a photo library integration.

**Conséquences** — `coverUrl` remains a dead field until a phase adds real
image covers; `PageCoverBand`/`PageCoverPickerContent` will need a second
branch (`coverUrl != null`) at that point.

---

## Slash command transforms the current block; doesn't insert a new one

**Contexte** — The fidelity requirement's Notion-like page navigation asks
for a "/" command. The existing "insert a block" flow
(`CreateBlockUseCase`) always appends a *new* block after a given one —
using it for "/" would leave a stray empty paragraph behind the newly
typed block, which is not how Notion's own "/" behaves (it transforms the
line being typed).

**Décision** — Add `BlockRepository.updateType` / `ChangeBlockTypeUseCase`,
which changes an existing block's `type` in place (keeping its `position`
and `content`). `PageDetailScreen` detects a trailing "/" character typed
into a block, strips it, and opens the same type-picker sheet used for
inserts — but wires its result to `onChangeBlockType` instead of
`onInsertBlock` when triggered this way.

**Raisons** — Reusing one repository method for two different user intents
(insert vs. transform) would need a magic "replace in place" flag threaded
through `createBlock`; a second, single-purpose method is more honest
about what's actually happening and simpler to test in isolation (see
`ChangeBlockTypeUseCaseTest`).

**Conséquences** — None yet; this is the first Phase 4+ use case that
mutates a block's type after creation, opening the door to a future
"change type" entry in the block's action menu too (not added this pass —
no caller needs it besides the slash command yet).

---

## Block position is a gap-based `Long`, not a shifted integer index

**Contexte** — Phase 4's block editor needs to insert a block between any
two existing blocks (from each block's "Insérer en dessous" action) without
re-numbering every later block on every insert.

**Décision** — `Block.position` is a `Long`. The first block gets `1000`;
appending sets `last.position + 1000`; inserting between two blocks sets
`(before.position + after.position) / 2`. `BlockRepositoryImpl` and
`FakeBlockRepository` implement this identically so the fakes used in
`core:domain`'s tests exercise the same logic as the real DB path.

**Raisons** — A shifted-integer-index scheme (`0, 1, 2, ...`) needs an
update to every row after the insertion point; the gap scheme needs exactly
one write for an insert and two for a move (swap two positions). Simpler
and cheaper than a fractional-position library for the scale a single-user
local demo needs.

**Conséquences** — Repeatedly inserting between the same two blocks halves
the remaining gap each time; with a `Long` and a starting gap of 1000 this
takes ~63 inserts-between-the-same-pair before collapsing, which is not
worth guarding against before a real usage pattern shows it matters.

---

## `Block.checked`/`Block.language` are concrete fields, not the brief's generic `properties` map

**Contexte** — The brief's `Block` entity has a generic `properties` blob
for type-specific data. Only two of Phase 4's implemented types need extra
data: `CHECKLIST` (a checked flag) and `CODE` (a language, currently unused
by any screen).

**Décision** — Add `checked: Boolean` and `language: String?` directly to
`core:model.Block` and the Room `BlockEntity`, instead of a serialized
key-value column.

**Raisons** — A generic map needs a `TypeConverter` (JSON serialization) on
a column this sandbox cannot compile-test; two concrete fields are simpler,
type-safe, and directly queryable. Two callers isn't enough to justify the
generic-map machinery — see `docs/DATA_MODEL.md`.

**Conséquences** — Revisit with a real key-value store (or
`kotlinx.serialization`-backed `TypeConverter`) if a third block type needs
its own custom property; two hardcoded fields would stop scaling at three.

---

## `material-icons-extended` moved to the `superapp.android.feature` convention plugin

**Contexte** — `core:designsystem` depends on
`androidx.compose.material:material-icons-extended` as `implementation`
(needed for its own components), which does not leak to `feature:*` modules
that also use icons outside the small curated `material-icons-core` set
(`feature:create`'s `StickyNote2`/`Draw`/`Groups` since Phase 1,
`feature:home`/`spaces`'s `AutoMirrored.Filled.Article`/`Message` since
Phase 2/3, and several of Phase 4's block-type icons). None of this has
ever been compiled, so this was a latent, undetected risk rather than a
confirmed failure.

**Décision** — Add `material-icons-extended` as an `implementation`
dependency in `AndroidFeatureConventionPlugin.kt`, so every `:feature:*`
module gets it automatically.

**Raisons** — Same reasoning `docs/DECISIONS.md` already applies to
`core:datastore`: a resource nearly every feature screen ends up needing is
better placed in the shared convention than re-added ad hoc per module each
time a screen happens to need an icon outside the curated set.

**Conséquences** — Slightly larger method count per feature module (mitigated
by R8/resource shrinking in release builds, per `superapp.android.application`).
The first real build should confirm whether any icon used since Phase 1
was, in fact, only in the extended set — if `assembleDebug` succeeds with no
"unresolved reference" on an icon, this was a preventive fix, not a live bug.

---

## Feature modules depend on `core:datastore` directly, not only through `core:domain`

**Contexte** — `ARCHITECTURE.md` documents that `feature:*` modules reach
data through `core:domain`'s use cases/repository interfaces. Phase 1 needed
`feature:settings` to read/write theme + dynamic-color preferences, which
live in `core:datastore`.

**Décision** — Add `core:datastore` to the `superapp.android.feature`
convention plugin's dependency set, alongside `core:domain`, and define
`UserPreferencesRepository` directly in `core:datastore` rather than behind
a separate interface-in-`core:domain`/impl-in-`core:datastore` split.

**Raisons** — User preferences are a thin, reactive read/write surface with
no real business logic (no validation, no cross-entity rules, no
authorization) — the interface-in-domain indirection exists to decouple
business rules from persistence choices, and there are no business rules
here to decouple. This mirrors how Now-in-Android-style projects treat their
`UserDataRepository`. Business entities (Post, Task, Page, ...) keep the
strict `core:domain`-mediated path once they land in Phase 2+.

**Conséquences** — `core:datastore` is now part of every feature module's
dependency graph, not just the ones that use it today; acceptable since
preferences (theme, notifications, accessibility, sync settings) are
something essentially every feature ends up reading eventually.

---

## Apply `org.jetbrains.kotlin.android` explicitly instead of relying on AGP 9's built-in Kotlin

**Contexte** — AGP 9.0+ compiles Kotlin without requiring the
`org.jetbrains.kotlin.android` plugin ("built-in Kotlin"). The Compose
compiler plugin and KSP2 are documented and battle-tested against the
classic explicit-KGP setup; built-in Kotlin's interaction with the Compose
compiler Gradle plugin and KSP is new (April 2026) and could not be verified
by an actual build in this environment (see the network limitation below).

**Décision** — `superapp.android.*` convention plugins explicitly apply
`org.jetbrains.kotlin.android`, which takes precedence over AGP's built-in
Kotlin.

**Raisons** — Known-compatible with KSP2 and the Compose compiler plugin;
easy to remove later once built-in Kotlin's interaction with those two
plugins is confirmed stable in a networked environment.

**Conséquences** — One extra plugin application per module vs. the AGP 9
default path; revisit in Phase 11 stabilization once a real build has run.

---

## Use `androidx.navigation:navigation-compose` (2.9.8) instead of Navigation 3

**Contexte** — The brief asks for "Navigation 3 stable." As of this session,
`androidx.navigation3` ships only alpha/rc artifacts
(`navigation3-runtime:1.0.0-alpha10`, `1.1.0-alpha01`); there is no stable
1.0.0 release yet (verified via web search, 2026-07-12).

**Décision** — Build on the stable Navigation Compose 2.9.8 (type-safe,
`@Serializable`-backed routes) instead, structured so the route contracts in
`core:navigation` (`AppRoute` sealed hierarchy, one `@Serializable` data
class/object per destination) map almost directly onto Navigation 3's
`NavKey` model.

**Raisons** — Rule: never add an alpha dependency for convenience; Navigation
3 is not "stable" as of today regardless of the brief's phrasing.

**Conséquences** — When Navigation 3 reaches 1.0.0 stable, migrate
`core:navigation` and the nav host in `:app`; the route-contract module
boundary was chosen specifically to make that swap local to those two
places.

---

## Use Room 2.8.4 instead of Room 3.0.0

**Contexte** — Room 3.0.0 went stable on 2026-07-01 (11 days before this
session) under a new `androidx.room3` namespace: KMP-first, Kotlin-only
codegen, all-suspend DAOs. Room 2.x is in maintenance mode but still
receiving stable patch releases (2.8.4, March 2026).

**Décision** — Use Room 2.8.4 for the initial scaffold.

**Raisons** — The brief explicitly allows this: "if Room 3.0 stable is
properly supported by the environment and chosen dependencies, use it;
otherwise use the latest stable Room 2.x." Room 3.0 is 11 days old, changes
the artifact namespace, and this session cannot build-test the interaction
with Paging 3, WorkManager, or Hilt against it. Room 2.x is the
lower-risk choice for a foundation other modules will build on immediately.

**Conséquences** — Revisit in Phase 11 (or sooner) once Room 3.0's ecosystem
support (Paging, migration tooling) has had more time to mature and can be
verified with a real build.

---

## Network/SDK access blocker — Phase 0 build could not be verified

**Contexte** — This session's egress policy blocks `dl.google.com` (Android
SDK Manager and the actual host `maven.google.com` redirects to for
artifact downloads), and `github.com` downloads are restricted to the
`safetyadrien/superapp` repository, which blocks fetching the Gradle 9.4.1
distribution (hosted at
`github.com/gradle/gradle-distributions/releases`). No Android SDK is
installed locally either.

**Décision** — Build the complete Phase 0 scaffold (all modules, convention
plugins, version catalog, docs, CI) using verified-current stable version
numbers (via web search against official release notes / Maven Central),
without being able to run `./gradlew assembleDebug` / `test` / `lint`
successfully in this session.

**Raisons** — Rule 25: "in case of network or SDK blockage, continue every
task that can be done locally and document the blocker precisely" rather
than stopping or fabricating a passing build.

**Conséquences** — The very first action in a networked environment must be
`./gradlew projects` followed by `assembleDebug`, `test`, `lint`, fixing
whatever surfaces — version catalog entries here were chosen carefully but
are not guaranteed to resolve without adjustment (see
`docs/DEPENDENCIES.md` for the confidence level on each version). See
`PROJECT_STATUS.md` for the exact commands run and their exact output.

---

## Collection entries are pages, not a parallel entity

**Contexte** — The brief is explicit: "chaque entrée de base de données
doit également être une page." A database entry needs a title, an icon, and
free-form block content in addition to its typed property values.

**Décision** — `Page` gained `collectionId: String?`. When set, that page
*is* a collection entry — its title/icon/blocks are the existing `Page`/
`Block` machinery, unchanged. `CollectionEntry` (`core:model`) is not a
Room table; it's `Page` plus a `Map<String, String>` of that page's
`CollectionPropertyValue` rows, assembled at the repository boundary
(`CollectionRepositoryImpl.observeEntries`).

**Raisons** — Building a second title/icon/content system for entries would
duplicate `PageDao`/`BlockDao`/`PageDetailScreen` for no benefit — an entry
opens in the exact same page editor as any other page, just with a
properties section prepended (`feature:editor`'s `PropertyValueRow`,
conditional on `page.collectionId != null`).

**Conséquences** — An entry can be navigated to and edited from anywhere a
regular page can (`AppRoute.PageDetail`), which is the desired Notion-like
behavior. It also means deleting a collection does not cascade to its
entries' pages automatically the way `CollectionPropertyValueEntity`'s
foreign keys cascade — `deleteCollection` isn't implemented yet (not in
`CollectionRepository`'s surface), so this gap is latent rather than active,
but worth flagging before that method is added.

---

## `SELECT`/`MULTI_SELECT`/`STATUS` values are stored as option labels, not option ids

**Contexte** — A `CollectionPropertyValue.value` needs to identify which
option(s) of a `SELECT`/`MULTI_SELECT`/`STATUS` property an entry has
chosen.

**Décision** — Store the option's `label` text directly (comma-joined for
`MULTI_SELECT`), not its `id`. `CollectionViewEngine`'s filter/sort and
every rendering site (`CollectionEntryRow`, `PropertyValueRow`) read the
stored string as the display value with no lookup.

**Raisons** — An id-based scheme needs an `optionId → label` join at every
read site (list rows, Kanban cards, the property editor, `CollectionViewEngine`'s
filter-by-value and group-by-value) for a feature that, at this scale, has
no real benefit from the indirection — no reference integrity engine, no
option-rename-cascades-to-old-values requirement in the brief.

**Conséquences** — Renaming or deleting an option does not retroactively
update entries that already stored the old label — a `MULTI_SELECT` value
list can end up containing a label matching no current option
(`CollectionPropertyDao.deleteOption` doesn't touch
`collection_property_values` at all). Acceptable at the current scale; an
id-based scheme with a cascade-on-rename/cascade-on-delete would be the fix
if this becomes a real problem.

---

## Databases scope cut: 3 of 5 view types, 14 of ~18 property types, single-condition filter/sort

**Contexte** — The brief's database spec asks for Table/List/Kanban/Gallery/
Calendar views, ~18 property types, compound (multi-condition) filtering and
sorting, and per-view (not just per-collection) property visibility.

**Décision** — Built Table, List, and Kanban views (`CollectionViewType`
has no `GALLERY`/`CALENDAR` case); 14 property types (`CollectionPropertyType`
— see `docs/DATA_MODEL.md` for exactly which 4 are missing and why); one
sort key and one filter condition per `CollectionView`, not compound;
property visibility (`CollectionProperty.visible`) is a single global flag,
not one flag per view.

**Raisons** — Same "document every scope decision rather than fake it"
discipline as every prior phase. Gallery needs image thumbnails (no image
loader pulled in yet, same reason `Page.coverUrl` is unused); Calendar needs
a date-grid renderer with no existing precedent in this codebase to build
on quickly. `FILE`/`RELATION` property types need an attachment picker and
a cross-collection entry picker respectively; `AGGREGATION`/`FORMULA` are
explicitly "ultérieure" in the brief itself; `LAST_EDITED_BY` needs
edit-attribution tracking `Page` doesn't have. Compound filter/sort and
per-view visibility are meaningfully more UI (multi-row condition builders)
for a first pass that already covers the validity-criteria bar in
`docs/FIDELITY.md` ("des vues configurables (au moins Table + Liste), avec
tri et filtre simples").

**Conséquences** — `ViewConfigContent` only exposes one sort/filter row —
switching views is the only way to see the same collection sliced two
different ways. Widening to compound conditions later means changing
`CollectionView`'s single `sortPropertyId`/`filterPropertyId` fields to
lists (and `CollectionViewEngine.apply`'s `filter`/`sort` to fold over
them), not a redesign — the single-condition version was chosen to be a
strict subset of that shape, not a dead end.
