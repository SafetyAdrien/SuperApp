# Design system

Lives in `core:designsystem`. Material 3 is the technical foundation; the
visual identity is its own (calm productivity-tool structure + simple
social interactions + moderate density), not generic Material 3 and not a
clone of Notion/Bluesky/Discord/Figma.

No screen-level composable ever hardcodes a hex color, spacing dp, corner
radius, or animation duration — always through `SuperAppTheme` /
`SuperAppColors` / `SuperAppTypography` / `SuperAppShapes` /
`SuperAppSpacing` / `SuperAppElevation` / `SuperAppMotion`.

## Color

Light and dark palettes (Primary, Primary Container, On Primary,
Background, Surface, Surface Variant, Surface Elevated, Text
Primary/Secondary/Tertiary, Border, Divider, Success, Warning, Error, Info)
are fixed in the brief and will be encoded as `SuperAppColors` in Phase 1.
Three theme modes are supported: system, light, dark — plus an independent,
user-toggleable "use device dynamic color" (Material You) setting that,
when off, keeps the app's own brand palette regardless of wallpaper.

## Type

System font (no custom typeface bundled). Hierarchy: Display Large,
Headline Large/Medium, Title Large/Medium, Body Large/Medium/Small, Label
Large/Medium/Small. Titles stay modest in size; body text in posts/pages/
messages stays comfortable at normal-to-larger sizes.

## Spacing / shape / elevation / motion

- Spacing scale: 2/4/8/12/16/20/24/32/40/48/64dp; 16dp is the default screen
  horizontal inset.
- Corner radii: Small 8dp, Medium 12dp, Large 16dp, Extra Large 24dp, Pill
  50%.
- Elevation stays subtle — prefer a surface-color/border/spacing change over
  a shadow; real elevation reserved for floating elements (FAB, sheets).
- Motion durations: Fast 120ms, Standard 180ms, Emphasized 240ms; no
  permanent decorative animation; respects the system reduce-motion setting
  via `SuperAppMotion`.

## Component inventory (Phase 1)

`SuperTopAppBar`, `SuperBottomNavigation`, `SuperNavigationItem`,
`SuperPrimaryButton`, `SuperSecondaryButton`, `SuperTertiaryButton`,
`SuperIconButton`, `SuperFloatingActionButton`, `SuperTextField`,
`SuperSearchField`, `SuperCard`, `SuperListItem`, `SuperAvatar`,
`SuperAvatarGroup`, `SuperBadge`, `SuperChip`, `SuperTabs`,
`SuperSegmentedControl`, `SuperBottomSheet`, `SuperDialog`, `SuperSnackbar`,
`SuperTooltip`, `SuperDivider`, `SuperEmptyState`, `SuperErrorState`,
`SuperOfflineBanner`, `SuperSyncIndicator`, `SuperLoadingIndicator`,
`SuperShimmer`, `SuperContextMenu`, `SuperAttachmentPreview`,
`SuperUserRow`, `SuperEntityReference`.

Each component: a clear Compose API, sensible defaults, enabled/disabled
states, loading state where relevant, dark-theme support, accessibility
semantics, and at least one `@Preview` (plus a dark `@Preview` for the
primary components).

**Status**: not yet implemented — `core/designsystem` currently contains
only a placeholder file. This is Phase 1 work.
