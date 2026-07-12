# Product

One account, one profile, one set of spaces, one permission system, one
design system, one navigation shell, one search engine, one notification
system, one data layer. Any entity (task, page, post, canvas, message) can
surface in multiple contexts — a task shown in a page, a project, the
activity feed, and search results is the same row, not a copy.

Tone: the structure and calm of a productivity tool (Notion-like pages and
blocks), the simplicity of a modern social feed (Bluesky-like), some
community mechanics (Discord-like channels/spaces), some contextual,
lightweight canvas behavior (Figma-like) — with its own visual identity.
Never a pixel-for-pixel clone of any of the above; no borrowed logos or
proprietary assets.

## Bottom navigation (5 destinations)

1. **Accueil** — social feed (Pour vous / Abonnements / Activité tabs)
2. **Espaces** — recent/favorite/personal/shared spaces, pages, projects
3. **Créer** — visually central, opens a bottom sheet (not a blank screen):
   new post, page, note, task, project, message, space, canvas
4. **Messages** — direct messages, groups, space channels
5. **Profil** — own profile, edit, settings entry point

## Contextual top bar

Not the same actions on every screen: logo/title, back, search,
notifications, contextual menu, sync state, space avatar, save/share/edit —
picked per screen, not all shown everywhere.

## MVP acceptance (no backend required)

The app must let a user, entirely offline against the local demo account:
launch on Android 12, finish or skip onboarding, use the demo account,
navigate all five destinations, view the feed, create a post, create a
space, create a page, add/reorder blocks, create a project, create a task,
open a conversation, send a local message, search their content, view
notifications, edit their local profile, switch theme, and survive an
app kill/relaunch without data loss — plus `assembleDebug`, `test`, and
`lint` passing. Full functional scope: `README.md` §"Feature scope" (see the
project brief for the exhaustive per-phase feature list; this file tracks
the product summary, `PROJECT_STATUS.md` tracks what is actually built).
