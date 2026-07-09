package com.termux.view;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TerminalViewScrollModeTest {

    @Test
    public void terminalOutputModeRoutesTouchDragToTerminalOutput() {
        assertTrue(TerminalView.isTerminalOutputDragMode(TerminalViewClient.TERMINAL_DRAG_MODE_TERMINAL_OUTPUT));
        assertFalse(TerminalView.isTerminalOutputDragMode(TerminalViewClient.TERMINAL_DRAG_MODE_DEFAULT));
    }

    @Test
    public void terminalOutputModeUsesPageKeysForAlternateBufferTouchDrag() {
        assertTrue(TerminalView.shouldUsePageKeysForAlternateBufferTouchDrag(TerminalViewClient.TERMINAL_DRAG_MODE_TERMINAL_OUTPUT));
        assertFalse(TerminalView.shouldUsePageKeysForAlternateBufferTouchDrag(TerminalViewClient.TERMINAL_DRAG_MODE_DEFAULT));
    }
}
