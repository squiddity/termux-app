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
- When the toggle is on, route touch drag/fling to Termux transcript scrollback instead of sending app-level DPAD up/down or mouse-wheel events.
- Preserve `PGUP` / `PGDN` extra keys as an app-level fallback; they were observed to move scroll position in Pi.

## Intentional trade-off

The first drag experiment prioritizes shell/Pi output review over full-screen app compatibility. Upstream `less` drag works partly because Termux sends app-level up/down events in fallback paths; this setting intentionally disables that behavior for touch drag. Revisit `less` and other alternate-screen apps after validating Pi behavior.

## Implementation units

### U1. Add drag mode setting

- Add `terminal_output_touch_drag` shared preference.
- Add Settings switch text and XML.
- Expose `getTerminalDragMode()` through `TerminalViewClient` and Termux client implementations.

### U2. Route touch drag through transcript mode

- Add `doTouchScroll()` in `TerminalView`.
- Use `doTouchScroll()` for finger drag/fling.
- In terminal-output mode, scroll transcript rows directly.
- Keep `doScroll()` as the default app-aware path for upstream behavior and physical mouse wheel.

### U3. Validate on device

Manual validation required:

- Shell/Pi drag reviews output instead of cycling command history.
- `PGUP` / `PGDN` still work in Pi.
- Toggle off restores upstream/default drag behavior.
- `less` regression is documented and accepted for this experiment.
- Fresh install/upgrade default keeps the setting off.

## Tests

- `terminal-view/src/test/java/com/termux/view/TerminalViewScrollModeTest.java` covers terminal-output mode selection.

## Out of scope

- Custom keyboard/input modes.
- Side-by-side package rename.
- Broad redesign of extra keys or app-specific drag policies.
