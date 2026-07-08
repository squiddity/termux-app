package com.termux.shared.termux.settings.properties;

import static org.junit.Assert.assertEquals;

import com.termux.view.TerminalViewClient;

import org.junit.Test;

public class TermuxSharedPropertiesTest {

    @Test
    public void terminalInputModeDefaultsToCurrentForMissingOrInvalidValues() {
        assertEquals(TerminalViewClient.TERMINAL_INPUT_MODE_CURRENT,
            TermuxSharedProperties.getTerminalInputModeInternalPropertyValueFromValue(null));
        assertEquals(TerminalViewClient.TERMINAL_INPUT_MODE_CURRENT,
            TermuxSharedProperties.getTerminalInputModeInternalPropertyValueFromValue("invalid"));
    }

    @Test
    public void terminalInputModeMapsValidLiteralValues() {
        assertEquals(TerminalViewClient.TERMINAL_INPUT_MODE_CURRENT,
            TermuxSharedProperties.getTerminalInputModeInternalPropertyValueFromValue("current"));
        assertEquals(TerminalViewClient.TERMINAL_INPUT_MODE_DIRECT_GBOARD,
            TermuxSharedProperties.getTerminalInputModeInternalPropertyValueFromValue("direct-gboard"));
    }

    @Test
    public void terminalDragModeDefaultsToDefaultForMissingOrInvalidValues() {
        assertEquals(TerminalViewClient.TERMINAL_DRAG_MODE_DEFAULT,
            TermuxSharedProperties.getTerminalDragModeInternalPropertyValueFromValue(null));
        assertEquals(TerminalViewClient.TERMINAL_DRAG_MODE_DEFAULT,
            TermuxSharedProperties.getTerminalDragModeInternalPropertyValueFromValue("invalid"));
    }

    @Test
    public void terminalDragModeMapsValidLiteralValues() {
        assertEquals(TerminalViewClient.TERMINAL_DRAG_MODE_DEFAULT,
            TermuxSharedProperties.getTerminalDragModeInternalPropertyValueFromValue("default"));
        assertEquals(TerminalViewClient.TERMINAL_DRAG_MODE_TERMINAL_OUTPUT,
            TermuxSharedProperties.getTerminalDragModeInternalPropertyValueFromValue("terminal-output"));
        assertEquals(TerminalViewClient.TERMINAL_DRAG_MODE_ACTIVE_APP,
            TermuxSharedProperties.getTerminalDragModeInternalPropertyValueFromValue("active-app"));
    }
}
