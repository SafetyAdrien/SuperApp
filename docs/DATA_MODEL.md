# Data model

Business models live in `core:model` as plain Kotlin data classes
(`kotlinx.serialization`-annotated for the network/local-demo-seed path);
Room entities in `core:database` are a separate, persistence-shaped
representation mapped to/from `core:model` types at the repository boundary
— the UI layer never sees a Room entity directly.

All ids are string UUIDs.

## Entities (summarized; see the project brief for full field lists)

- **Profile** — id, handle, displayName, biography, avatarUrl, bannerUrl,
  timestamps.
- **Space** — id, name, description, icon, ownerId, visibility
  (`PRIVATE` / `INVITE_ONLY` / `PUBLIC`), timestamps.
- **SpaceMember** — spaceId, profileId, role (`OWNER` / `ADMIN` / `EDITOR` /
  `MEMBER` / `GUEST`), joinedAt.
- **Page** — id, spaceId, parentPageId, title, icon, coverUrl, coverColorKey,
  createdBy, timestamps, archivedAt.
- **Block** — id, pageId, parentBlockId, type, position, content,
  properties, timestamps. Types: `PARAGRAPH`, `HEADING_1/2/3`,
  `BULLETED_LIST`, `NUMBERED_LIST`, `CHECKLIST`, `QUOTE`, `CALLOUT`,
  `DIVIDER`, `CODE`, `IMAGE`, `FILE`, `LINK`, `TASK_REFERENCE`,
  `PAGE_REFERENCE`, `CANVAS_REFERENCE`.
- **Post** — id, authorId, spaceId, text, replyToPostId, quotedPostId,
  visibility, timestamps.
- **Reaction** — id, entityType, entityId, profileId, reaction, createdAt —
  reactions are polymorphic over `(entityType, entityId)`, not per-table.
- **Task** — id, spaceId, projectId, title, description, status (`BACKLOG` /
  `TODO` / `IN_PROGRESS` / `BLOCKED` / `DONE` / `CANCELLED`), priority
  (`NONE`…`URGENT`), assigneeId, dueAt, timestamps.
- **Conversation** — id, type (`DIRECT` / `GROUP` / `CHANNEL`), title,
  spaceId, timestamps.
- **Message** — id, conversationId, authorId, content, replyToMessageId,
  createdAt, editedAt, deletedAt (soft delete). Client-side send state:
  `DRAFT` / `SENDING` / `SENT` / `FAILED` / `EDITED` / `DELETED`.
- **AppNotification** — id, recipientId, actorId, type, entityType,
  entityId, isRead, createdAt.
- **CanvasDocument** / **CanvasNode** — id, canvasId/spaceId, type (`TEXT`,
  `NOTE`, `RECTANGLE`, `ELLIPSE`, `IMAGE`, `CONNECTOR`), x/y/width/height/
  rotation/zIndex, properties.
- **Collection** — id, spaceId, title, icon, createdBy, timestamps. A
  Notion-like database: a schema (`CollectionProperty`) applied to a set of
  pages.
- **CollectionProperty** — id, collectionId, name, type (14 of the brief's
  ~18: `TEXT`, `NUMBER`, `SELECT`, `MULTI_SELECT`, `STATUS`, `DATE`,
  `CHECKBOX`, `URL`, `EMAIL`, `PHONE`, `PERSON`, `CREATED_AT`, `UPDATED_AT`,
  `CREATED_BY` — `FILE`/`RELATION` deferred, `AGGREGATION`/`FORMULA` are
  explicitly "ultérieure" in the brief, `LAST_EDITED_BY` needs
  edit-attribution tracking `Page` doesn't have), position, visible.
- **CollectionPropertyOption** — id, propertyId, label, colorKey, position —
  the fixed choice list backing `SELECT`/`MULTI_SELECT`/`STATUS`.
- **CollectionPropertyValue** — (pageId, propertyId) composite key, value
  (nullable string) — Room-only, never modeled in `core:model` directly;
  exposed to the rest of the app as `CollectionEntry.values: Map<String,
  String>`. A property with no row for a given entry is simply absent, not
  stored as an explicit empty value.
- **CollectionEntry** — not its own table: a `Page` (with `collectionId`
  set) plus its `CollectionPropertyValue` rows, joined at the repository
  boundary. "Chaque entrée de base de données doit également être une
  page" — see below.
- **CollectionView** — id, collectionId, name, type (`TABLE` / `LIST` /
  `KANBAN` — `GALLERY`/`CALENDAR` deferred), position, sortPropertyId,
  sortDescending, groupPropertyId (Kanban only), filterPropertyId,
  filterOperator, filterValue. One sort key and one filter condition per
  view, not the brief's full compound filter/sort — see `docs/DECISIONS.md`.

## Cross-entity references

Anything can reference anything by `(entityType, entityId)`: a post can
quote a page or task, a comment surfaces in a space's activity feed, a
canvas attaches to a page, a message can deep-link to any entity via the
`superapp://` scheme (see `docs/NAVIGATION.md`). This is why reactions and
notifications are modeled polymorphically rather than one table per parent
type.

**Status**: Profile, Post (+ `PostWithAuthor` read model), and Reaction are
implemented in `core/model`, with Room entities/DAOs/mappers in
`core/database` (`SuperAppDatabase`, schema export configured to
`core/database/schemas/` — the directory only appears once KSP actually
runs, which it hasn't in this sandbox; see `PROJECT_STATUS.md`). Reactions
are stored polymorphically as designed
(`entityType` + `entityId`), even though only `EntityType.POST` has a real
producer so far. The feed query joins posts + author + correlated
reaction/reply-count subqueries in one `PagingSource<Int, PostFeedRow>` —
see `core/database/.../dao/PostDao.kt`.

Phase 3 added Space, SpaceMember, and Page the same way: `SpaceDao`'s
`observeSpacesForMember`/`observeSpace` join a space with two correlated
subqueries (member count, non-archived page count) into
`SpaceWithStatsRow` — the same shape as `PostFeedRow`, just without a
`ProfileEntity` join since a space browser row doesn't need one. `Page` has
no Room migration story of its own yet because `SuperAppDatabase` is still
version 1 (see the comment on `SuperAppDatabase`) — `Page` is just title/
icon/cover metadata plus a `parentPageId` for future nesting that no screen
navigates into yet (`PageDao.observePagesForSpace` only returns top-level
pages).

Phase 4 added Block, the page body itself: `PARAGRAPH`, `HEADING_1/2/3`,
`BULLETED_LIST`, `NUMBERED_LIST`, `CHECKLIST`, `QUOTE`, `CALLOUT`,
`DIVIDER`, `CODE` are implemented; `IMAGE`, `FILE`, `LINK`,
`TASK_REFERENCE`, `PAGE_REFERENCE`, `CANVAS_REFERENCE` are deferred to the
phases that add what they'd reference. Two deviations from the brief's
abstract "properties" blob: `Block.checked` (CHECKLIST) and `Block.language`
(CODE, unused by any screen yet) are concrete fields on `core:model.Block`
instead of a generic key-value map — simpler and Room-friendly (no
`TypeConverter`/JSON column to get right without a compiler), revisit if a
third block type needs its own custom property. `Block.position` is a `Long`
with gap-based spacing (new blocks land at `(before + after) / 2`, or
`last + 1000` at the end) computed in `BlockRepositoryImpl`/
`FakeBlockRepository` identically, so inserting between two blocks never
requires shifting every row after it — see `docs/DECISIONS.md`.

Remaining entities (Task, Conversation, Message, AppNotification,
CanvasDocument, CanvasNode) land with their owning phase.

**Retrofit (same session, after the fidelity requirement was added — see
`docs/FIDELITY.md`)**: `Page` gained `coverColorKey`, a solid-color-cover
token (one of `core:designsystem`'s `SuperAppCoverColors` keys) distinct
from `coverUrl` (a real photo cover, still unused — needs an image loader
this codebase doesn't pull in yet). `icon` (already in the model since
Phase 3, unused until now) is a single emoji character, editable via
`feature:editor`'s icon picker. `PageRepository` gained
`observeRecentPages` (workspace-home "Récents", reactive on the current
profile the same way `SpaceRepository.observeSpaces` is) and `deletePage`
(cascades to that page's blocks via `BlockEntity`'s existing foreign key).
`BlockRepository` gained `updateType`, which changes an existing block's
`type` without touching its `position`/`content` — this is the "/" command's
effect (see `PageDetailViewModel`), distinct from `createBlock` which only
ever appends a new block.

**New Phase 4 (databases)**: added Collection, CollectionProperty,
CollectionPropertyOption, CollectionPropertyValue, and CollectionView (see
above). `Page` gained `collectionId: String?` — set when a page is a
collection entry, null for an ordinary page; `PageDao.observePagesForCollection`
is the entry-list query. `CollectionRepositoryImpl.observeEntries` combines
that page list with `CollectionPropertyValueDao.observeValuesForCollection`
(one query, joined against `collection_properties` to scope by
`collectionId` even though the value table itself only has `pageId`/
`propertyId`) into `CollectionEntry` — no dedicated entry table exists.
`CollectionViewEngine` (`core:domain`) is a pure, Room/Flow-independent
object applying a view's filter then sort (`apply`) or grouping entries by a
property's value (`group`, Kanban's columns) — kept out of the repository so
it's cheap to unit test and reusable across the Table/List/Kanban
composables without duplicating the same `when`. `SELECT`/`MULTI_SELECT`/
`STATUS` property values are stored as the option's label text directly,
not its id (see `docs/DECISIONS.md`).
