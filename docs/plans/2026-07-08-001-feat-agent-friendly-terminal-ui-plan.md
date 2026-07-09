---
artifact_contract: ce-unified-plan/v1
artifact_readiness: implementation-ready
type: feat
status: active
product_contract_source: ce-plan-bootstrap
---

# Agent-Friendly Terminal Drag Plan

## Goal

Make Termux usable for reviewing shell/Pi output on phones without accidental command-history movement from finger drag.

## Current scope

- Keep upstream keyboard behavior unchanged.
- Add an opt-in Settings toggle: **Terminal I/O → Keyboard → Drag Reviews Terminal Output**.
- When the toggle is off, keep upstream/default drag behavior.
- When the toggle is on, keep upstream behavior except for the fallback path that maps drag to app-level DPAD up/down; send `PGUP` / `PGDN` there instead.
- Preserve `PGUP` / `PGDN` extra keys as an app-level fallback; they were observed to move scroll position in Pi.

## Intentional trade-off

The current drag experiment changes the fallback key choice rather than disabling app behavior wholesale. Upstream `less` drag works partly because Termux sends app-level up/down events in fallback paths; this setting sends page keys in that same path because `PGUP` / `PGDN` were observed to move scroll position in Pi without cycling command history.

## Implementation units

### U1. Add drag mode setting

- Add `terminal_output_touch_drag` shared preference.
- Add Settings switch text and XML.
- Expose `getTerminalDragMode()` through `TerminalViewClient` and Termux client implementations.

### U2. Route touch drag through transcript mode

- Add `doTouchScroll()` in `TerminalView`.
- Use `doTouchScroll()` for finger drag/fling.
- In terminal-output mode, keep main-buffer transcript scrolling and mouse-tracking behavior unchanged.
- In terminal-output mode, replace alternate-buffer DPAD fallback with page keys.
- Keep `doScroll()` as the default app-aware path for upstream behavior and physical mouse wheel.

### U3. Validate on device

Manual validation required:

- Shell/Pi drag reviews output instead of cycling command history.
- `PGUP` / `PGDN` still work in Pi.
- Toggle off restores upstream/default drag behavior.
- `less` still moves in the fallback path, now via page keys rather than arrow keys.
- Fresh install/upgrade default keeps the setting off.

## Tests

- `terminal-view/src/test/java/com/termux/view/TerminalViewScrollModeTest.java` covers terminal-output mode selection.

## Out of scope

- Custom keyboard/input modes.
- Side-by-side package rename.
- Broad redesign of extra keys or app-specific drag policies.
