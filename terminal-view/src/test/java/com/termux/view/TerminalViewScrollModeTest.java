package com.termux.view;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TerminalViewScrollModeTest {

    @Test
    public void terminalOutputModeRoutesTouchDragToTerminalOutput() {
        assertTrue(TerminalView.isTerminalOutputDragMode(TerminalViewClient.TERMINAL_DRAG_MODE_TERMINAL_OUTPUT));
        assertFalse(TerminalView.isTerminalOutputDragMode(TerminalViewClient.TERMINAL_DRAG_MODE_DEFAULT));
        assertFalse(TerminalView.isTerminalOutputDragMode(TerminalViewClient.TERMINAL_DRAG_MODE_ACTIVE_APP));
    }

    @Test
    public void activeAppModeRoutesTouchDragToActiveApp() {
        assertTrue(TerminalView.isActiveAppDragMode(TerminalViewClient.TERMINAL_DRAG_MODE_ACTIVE_APP));
        assertFalse(TerminalView.isActiveAppDragMode(TerminalViewClient.TERMINAL_DRAG_MODE_DEFAULT));
        assertFalse(TerminalView.isActiveAppDragMode(TerminalViewClient.TERMINAL_DRAG_MODE_TERMINAL_OUTPUT));
    }
}
