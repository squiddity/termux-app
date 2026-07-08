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

}
