# Permissions

Principle: request a permission only when the user triggers a feature that
needs it, never at first launch "just in case."

| Permission | Requested when | Status |
|---|---|---|
| `POST_NOTIFICATIONS` (API 33+) | User reaches a point where a notification would actually be shown (not on app start) | Not yet requested — no notification-producing feature exists yet (Phase 7) |

No other runtime permission is declared. In particular, this app does **not**
request location, contacts, microphone, camera, or Bluetooth permissions
unless and until a real, implemented feature needs one — none currently do.

## Preferred system pickers over broad permissions

- Photos/images: [Photo Picker](https://developer.android.com/training/data-storage/shared/photopicker)
  (`ActivityResultContracts.PickVisualMedia`), no `READ_MEDIA_IMAGES`.
- Arbitrary files: Storage Access Framework
  (`ActivityResultContracts.OpenDocument` / `CreateDocument`), no broad
  storage permission.
- Sharing content out: `Intent.ACTION_SEND` / `ACTION_SEND_MULTIPLE` via the
  system share sheet.
- Receiving shared content: an intent filter on `ACTION_SEND` /
  `ACTION_SEND_MULTIPLE`, added when the compose-post / attach-file flow is
  implemented.

## Manifest exported components

| Component | `exported` | Why |
|---|---|---|
| `MainActivity` | `true` | Launcher entry point + `superapp://` internal deep links |

Every other component added in later phases (workers, receivers) must set
`android:exported` explicitly and default to `false` unless it genuinely
needs to be reachable from outside the app.
