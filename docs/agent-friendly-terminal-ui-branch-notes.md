# Agent-Friendly Terminal UI Branch Notes

This document summarizes the current `plan/agent-friendly-terminal-ui` branch so future sessions can continue without reconstructing the decisions from chat history.

## Base and release builds

- Branch was rebased onto upstream tag `v0.119.0-beta.3`.
- Current working release tag after reverting side-by-side package rename: `v0.119.0-beta.3-agent-ui.3`.
- GitHub Actions release build succeeded here:
  - https://github.com/squiddity/termux-app/actions/runs/28980287794
  - https://github.com/squiddity/termux-app/releases/tag/v0.119.0-beta.3-agent-ui.3
- This branch adds `.github/workflows/manual_debug_release.yml` for manual/tagged debug APK releases.
  - Builds both package variants: `apt-android-7` and `apt-android-5`.
  - Publishes universal and ABI-specific debug APKs plus SHA256 sums to a GitHub release.
  - Manual dispatch accepts a release tag and prerelease flag; pushing `v*` tags also triggers the workflow.
- Earlier side-by-side package attempt (`com.termux.agent`, app label `Termux (agent build)`) built but hit bootstrap errors because Termux bootstrap paths are package-name/prefix sensitive. That commit was reverted.

## User-facing feature summary

This branch experiments with making Termux friendlier for agent/Pi usage on phones:

1. **Soft keyboard mode cycling**
   - The existing drawer keyboard button and existing `KEYBOARD` extra-key action now cycle through three states:
     1. Keyboard hidden.
     2. Keyboard shown with current/mainline terminal-safe input behavior.
     3. Keyboard shown with a direct Gboard-oriented text input profile intended to allow suggestions/autocomplete/swipe typing.
   - The direct input profile is experimental and Gboard-first; it is not a compatibility claim for all Android keyboards.
   - `termux.properties` now supports `terminal-input-mode = current` or `terminal-input-mode = direct-gboard`; default is `current`.
   - Runtime cycling stores an in-session override in `TermuxTerminalViewClient`, so users can switch modes without restarting Termux.
   - Ctrl+Alt+K still calls the older `onToggleSoftKeyboardRequest()` behavior unless intentionally changed later.

2. **Terminal output drag setting**
   - New Settings toggle: **Settings → Terminal I/O → Keyboard → Drag Reviews Terminal Output**.
   - When disabled, touch drag uses upstream/default behavior.
   - When enabled, touch drag only changes behavior in the fallback path where mouse tracking is not active: instead of sending DPAD/app-style scroll events that can cycle shell/Pi command history, drag moves Termux scrollback.
   - Explicit mouse-tracking app behavior is preserved, so apps such as `less` that already support drag/mouse scrolling should keep working.

3. **Default extra keys on beta base**
   - Upstream `v0.119.0-beta.3` changed the default extra keys to include `{key: 'DRAWER', popup: 'PASTE'}` and `SCROLL`.
   - This branch currently keeps that beta default unchanged; it does **not** replace `SCROLL` with `KEYBOARD`.
   - The drawer itself has a visible Keyboard button, and that button now uses the same keyboard mode cycle.

4. **README documentation**
   - `README.md` now has an **Experimental agent-friendly terminal controls** section.
   - It documents `terminal-input-mode`, the `KEYBOARD` extra-key cycle, and the **Drag Reviews Terminal Output** setting.
   - The wording intentionally frames the direct input profile as experimental/Gboard-first rather than a general Android keyboard compatibility promise.

## Main implementation files

### Keyboard/input mode

- `terminal-view/src/main/java/com/termux/view/TerminalViewClient.java`
  - Adds input mode constants:
    - `TERMINAL_INPUT_MODE_CURRENT`
    - `TERMINAL_INPUT_MODE_DIRECT_GBOARD`
  - Adds `getTerminalInputMode()`.

- `termux-shared/src/main/java/com/termux/shared/termux/settings/properties/TermuxPropertyConstants.java`
  - Adds `terminal-input-mode` property and valid values.

- `termux-shared/src/main/java/com/termux/shared/termux/settings/properties/TermuxSharedProperties.java`
  - Parses and exposes `terminal-input-mode`.

- `app/src/main/java/com/termux/app/terminal/TermuxTerminalViewClient.java`
  - Stores runtime input mode override in `mTerminalInputMode`.
  - Adds `onCycleSoftKeyboardModeRequest()` for the 3-state cycle.
  - Uses `InputMethodManager.restartInput()` after mode changes.

- `terminal-view/src/main/java/com/termux/view/TerminalView.java`
  - Adds `getTerminalSelectedInputType()`.
  - Direct Gboard mode returns a text-like `InputType` without `TYPE_NULL`, password variation, or no-suggestions flag.
  - `finishComposingText()` does not send composing text in direct mode, avoiding duplicate sends when Gboard finalizes swipe suggestions through `commitText()`.

- `app/src/main/java/com/termux/app/terminal/io/TermuxTerminalExtraKeys.java`
  - Existing `KEYBOARD` special action now calls `onCycleSoftKeyboardModeRequest()`.

- `app/src/main/java/com/termux/app/TermuxActivity.java`
  - Drawer keyboard button now also calls `onCycleSoftKeyboardModeRequest()`.

### Drag/scroll behavior

- `terminal-view/src/main/java/com/termux/view/TerminalViewClient.java`
  - Adds drag mode constants:
    - `TERMINAL_DRAG_MODE_DEFAULT`
    - `TERMINAL_DRAG_MODE_TERMINAL_OUTPUT`
  - Adds `getTerminalDragMode()`.

- `termux-shared/src/main/java/com/termux/shared/termux/settings/preferences/TermuxPreferenceConstants.java`
  - Adds shared preference key `terminal_output_touch_drag`.

- `termux-shared/src/main/java/com/termux/shared/termux/settings/preferences/TermuxAppSharedPreferences.java`
  - Adds getters/setters for the setting.

- `app/src/main/res/xml/termux_terminal_io_preferences.xml`
  - Adds the Settings switch.

- `app/src/main/java/com/termux/app/fragments/settings/termux/TerminalIOPreferencesFragment.java`
  - Wires the setting to shared preferences.

- `app/src/main/java/com/termux/app/terminal/TermuxTerminalViewClient.java`
  - `getTerminalDragMode()` derives from `shouldUseTerminalOutputTouchDrag()`.

- `terminal-view/src/main/java/com/termux/view/TerminalView.java`
  - Touch drag/fling now routes through `doTouchScroll()`.
  - `terminal-output` mode uses transcript scroll only when mouse tracking is not active.
  - Existing `doScroll()` remains the default app-aware route for physical mouse wheel, mouse tracking, alternate buffer, and normal behavior.

## Tests added

- `termux-shared/src/test/java/com/termux/shared/termux/settings/properties/TermuxSharedPropertiesTest.java`
  - Covers `terminal-input-mode` property parsing/default fallback.

- `terminal-view/src/test/java/com/termux/view/TerminalViewInputConnectionTest.java`
  - Covers current input behavior and direct Gboard input type flags.

- `terminal-view/src/test/java/com/termux/view/TerminalViewScrollModeTest.java`
  - Covers terminal-output drag mode decision logic and mouse-tracking preservation.

## Things learned / decisions made

- Manual APK testing found the keyboard cycle/direct input path good enough for now; future work can focus on smoothing autocomplete/correction behavior.
- Termux upstream already supports drag scrolling in apps such as `less`, including fallback paths that send DPAD/app-style up/down events when mouse tracking is not active.
- That fallback explains why shell/Pi sessions can cycle command history on drag: they receive up/down-style input instead of scrollback review.
- The current experiment for **Drag Reviews Terminal Output** intentionally disables touch-drag app-awareness and routes touch drag to Termux transcript scrollback so it never sends DPAD up/down or mouse-wheel events from finger drag. This prioritizes shell/Pi review behavior over `less` drag compatibility for now.
- `PGUP` / `PGDN` extra keys still send page keys through the normal terminal key path and were observed to move scroll position in Pi; they may remain the app-level fallback while drag behavior is tuned.
- A side-by-side package rename is not viable without a matching bootstrap built for the new package/data prefix.
- The beta branch's default extra keys differ from master; keep beta defaults unless explicitly changing them.

## Known caveats / next steps

- Manual device validation is still needed for:
  - Gboard autocomplete/correction polish beyond the current good-enough behavior.
  - The updated **Drag Reviews Terminal Output** behavior after disabling touch-drag app-awareness:
    - shell/Pi drag should review output rather than cycle command history;
    - `less` drag is expected to regress for now and should be revisited later;
    - `PGUP` / `PGDN` extra keys should continue to work as an app-level scroll fallback in Pi.
  - `terminal-input-mode` property reload behavior, including default fallback and switching between `current` / `direct-gboard`.
  - Fresh install and upgrade behavior for the new `terminal_output_touch_drag` preference default.
  - Installing and launching debug release assets from the manual GitHub release workflow on target devices.
- Decide whether this remains an experimental fork-only build or should be split into smaller upstreamable PRs.
- If direct input mode has Gboard composition issues, revisit `finishComposingText()`, `commitText()`, and replacement/correction handling in `TerminalView`.
- If drawer keyboard behavior should keep the old simple show/hide semantics, revert only the `TermuxActivity.setToggleKeyboardView()` call site; the extra-key `KEYBOARD` can still use the 3-state cycle.
- If side-by-side install is needed, investigate building a bootstrap/package set for a custom package prefix instead of only changing Android `applicationId`.
