# Fidelity requirements

Standing, cross-phase requirement (added when the brief was extended
2026-07-12, after Phase 4): the app must reproduce, with strong fidelity,
the **interface structure, navigation logic, touch behavior, visual
hierarchy, functional organization, content types, core interactions,
information density, and creation/editing mechanisms** of four reference
apps — one per major surface:

| Surface | Reference | What "fidelity" means here |
|---|---|---|
| Spaces/pages (workspace) | Notion Mobile | neutral surfaces, thin dividers, sober type, monochrome icons, generous spacing, secondary controls hidden until tapped/selected/long-pressed, bottom sheets for menus, collapsible sections, editable-in-place blocks |
| Home feed | Bluesky Mobile | top bar + horizontal feed tabs, paginated cards (avatar/name/handle/time/actions), pull-to-refresh, optimistic actions, a lightweight composer |
| Communities | Discord (renamed — never the word "Discord" in-app) | typed channels (text/forum/media/announcements/voice/stage/resources), categories, roles/permissions, threads |
| Design | Figma | infinite canvas (native rendering, no WebView/HTML canvas), selection, layers, a contextual property inspector, alignment/Auto Layout, native undo/redo commands |

**What fidelity is *not***: pixel-perfect cloning, or reusing any
reference app's logos, brand names, proprietary icons/illustrations,
copy, or source code. `docs/DECISIONS.md` and `PROJECT_STATUS.md`
document every deliberate deviation from a reference app's exact
behavior, same as any other design decision.

## Validity criteria (a screen existing is not "done")

- **Notion-like**: pages are hierarchical, blocks are editable in place,
  menus are contextual (bottom sheets, not always-visible toolbars), data
  persists, a future database view configuration would need to be real
  (not a static mock).
- **Bluesky-like**: the feed is paginated, scroll position survives
  navigation, posts are actually interactive (reply/react/repost, not
  just rendered), drafts persist, feed tabs are/will be configurable.
- **Discord-like**: channel *types* actually behave differently (a forum
  is not a chat with a different icon), permissions are computed (not
  hardcoded `true`), categories inherit permissions correctly, threads
  work independently of their parent channel.
- **Figma-like**: the canvas is genuinely pannable/zoomable (not a static
  image), zoom centers on the gesture, transformations persist, layers
  reflect the real scene graph, the inspector actually mutates the
  selected object's properties, undo/redo works, performance holds with
  hundreds of objects.

Per the brief's own priority order: architecture soundness and stability
first, then interaction fidelity, then mobile adaptation, then
performance, then accessibility, then visual fidelity, then secondary
features. Never trade stability for an incomplete visual imitation, and
never ship a static screen that only *looks* like the reference app —
every shipped element must be functional, per this project's standing
"no silent placeholders" rule (see `PROJECT_STATUS.md` §TODO justifiés).

## Mobile adaptation (all four surfaces)

This is Android-only, phone-first. Desktop reference-app layouts
(persistent multi-column sidebars, docked panels) are never reproduced
literally:

- sidebars → drawers or dedicated screens
- inspectors/property panels → bottom sheets
- complex menus → bottom sheets or secondary screens
- touch targets ≥ 48dp, gestures expected (long-press, swipe, pinch)
- never show every mode's controls at once — progressive disclosure by
  active context (page / feed / community / design)

## Status

Retrofitted into Phase 3/4's existing screens in the same session this
requirement was added (no separate "Phase R" in the numbered plan — see
`PROJECT_STATUS.md` for the exact diff): the workspace home
(`feature:spaces`'s `SpacesScreen`) became a collapsible-sections layout
(Récents/Favoris/Espaces/Pages privées/Pages partagées/Modèles/Corbeille);
the page editor (`feature:editor`'s `PageDetailScreen`) gained a cover/
icon header, a compact breadcrumb, a "⋮" page menu, a real "/" command
that transforms the current block, and long-press block selection. The
Bluesky-like feed already matched the required structure from Phase 2 and
only needed top-bar search/notification affordances added. Discord-like
communities and Figma-like canvas are still entirely unbuilt — they land
in their own future phases (see the brief's phase renumbering, not yet
reconciled 1:1 with `PROJECT_STATUS.md`'s phase history).
