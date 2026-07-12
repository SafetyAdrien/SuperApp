# Accessibility

Baseline every screen must meet, to be verified with TalkBack as each screen
lands (not yet applicable — no real screens exist before Phase 1):

- Touch targets ≥ 48dp, including in dense list rows.
- Contrast: body text meets WCAG AA against its surface in both the light
  and dark palettes defined in `docs/DESIGN_SYSTEM.md`.
- Every interactive element has a accessible label (`contentDescription` for
  icon-only buttons; no icon-only button ships without one).
- Focus order follows visual/reading order; no focus traps.
- Information is never conveyed by color alone (e.g. sync/error states pair
  color with an icon or text, not color only).
- Text scales correctly up to 200% (`fontScale`) without clipping or
  overlapping content — verified by testing key screens at large font
  scale, not just default.
- Respects `Settings.Global.ANIMATOR_DURATION_SCALE` / the system "remove
  animations" accessibility setting; `SuperAppMotion` (Phase 1,
  `core:designsystem`) reads this before running any non-essential
  animation.
- Haptic feedback is optional, never required to understand app state.
- Errors are announced (e.g. via `liveRegion`/`Snackbar` semantics), not
  only shown visually.

## How this will be tested

- Compose UI tests assert `contentDescription`/`semantics` on interactive
  elements as each screen is built (see `docs/TESTING.md`).
- Manual TalkBack pass on the five bottom-navigation destinations and the
  block editor before each phase is marked done in `PROJECT_STATUS.md`.
- Manual pass at 200% font scale on the feed, editor, and messages screens
  specifically, since they carry the most body text.
