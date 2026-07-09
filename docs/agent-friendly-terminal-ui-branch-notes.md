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

1. **Terminal output drag setting**
   - New Settings toggle: **Settings → Terminal I/O → Keyboard → Drag Reviews Terminal Output**.
   - When disabled, touch drag uses upstream/default behavior.
   - When enabled, touch drag keeps upstream behavior except for the fallback path that maps drag to DPAD/app-style up/down input.
   - In that fallback path, this branch sends `PGUP` / `PGDN` instead, matching the extra keys that move scroll position in Pi and avoiding command-history cycling from arrow keys.

2. **Default extra keys on beta base**
   - Upstream `v0.119.0-beta.3` changed the default extra keys to include `{key: 'DRAWER', popup: 'PASTE'}` and `SCROLL`.
   - This branch currently keeps that beta default unchanged; it does **not** replace `SCROLL` with `KEYBOARD`.
   - `PGUP` / `PGDN` extra keys remain available and were observed to move scroll position in Pi.

3. **README documentation**
   - `README.md` now has an **Experimental agent-friendly terminal controls** section documenting only **Drag Reviews Terminal Output**.

## Main implementation files

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
  - `terminal-output` mode keeps the normal app-aware route but maps the alternate-buffer fallback from DPAD up/down to page up/down.
  - Existing `doScroll()` remains the default app-aware route for physical mouse wheel and normal/upstream behavior when the setting is disabled.

## Tests added

- `terminal-view/src/test/java/com/termux/view/TerminalViewScrollModeTest.java`
  - Covers terminal-output drag mode decision logic.

## Things learned / decisions made

- The custom keyboard/input-mode work was removed from this branch after finding a keyboard that works well enough with upstream Termux behavior; this branch now focuses only on drag behavior.
- Termux upstream already supports drag scrolling in apps such as `less`, including fallback paths that send DPAD/app-style up/down events when mouse tracking is not active.
- That fallback explains why shell/Pi sessions can cycle command history on drag: they receive up/down-style input instead of scrollback review.
- `PGUP` / `PGDN` extra keys send page keys through the normal terminal key path and were observed to move scroll position in Pi.
- The current experiment for **Drag Reviews Terminal Output** changes only the arrow-key fallback to use page keys instead of DPAD up/down; main-buffer transcript scrolling and mouse-tracking behavior stay upstream.
- A side-by-side package rename is not viable without a matching bootstrap built for the new package/data prefix.
- The beta branch's default extra keys differ from master; keep beta defaults unless explicitly changing them.

## Known caveats / next steps

- Manual device validation is still needed for:
  - The updated **Drag Reviews Terminal Output** behavior after mapping fallback drag to page keys:
    - shell/Pi drag should review output rather than cycle command history;
    - `less` drag should still move, now via page keys in the fallback path;
    - `PGUP` / `PGDN` extra keys should continue to work as an app-level scroll fallback in Pi.
  - Fresh install and upgrade behavior for the new `terminal_output_touch_drag` preference default.
  - Installing and launching debug release assets from the manual GitHub release workflow on target devices.
- Decide whether this remains an experimental fork-only build or should be split into smaller upstreamable PRs.
- If side-by-side install is needed, investigate building a bootstrap/package set for a custom package prefix instead of only changing Android `applicationId`.
