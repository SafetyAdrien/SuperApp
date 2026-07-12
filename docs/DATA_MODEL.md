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
- **Page** — id, spaceId, parentPageId, title, icon, coverUrl, createdBy,
  timestamps, archivedAt.
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

## Cross-entity references

Anything can reference anything by `(entityType, entityId)`: a post can
quote a page or task, a comment surfaces in a space's activity feed, a
canvas attaches to a page, a message can deep-link to any entity via the
`superapp://` scheme (see `docs/NAVIGATION.md`). This is why reactions and
notifications are modeled polymorphically rather than one table per parent
type.

**Status**: not yet implemented — `core/model` and `core/database` each
currently contain only a placeholder file. Entities land incrementally,
starting with Profile/Post/Reaction in Phase 2.
