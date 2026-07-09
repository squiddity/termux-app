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
    public void terminalOutputModeAlwaysReplacesTouchDragWithTranscriptScroll() {
        assertTrue(TerminalView.shouldUseTranscriptForTouchDrag(TerminalViewClient.TERMINAL_DRAG_MODE_TERMINAL_OUTPUT));
        assertFalse(TerminalView.shouldUseTranscriptForTouchDrag(TerminalViewClient.TERMINAL_DRAG_MODE_DEFAULT));
    }
}
