package com.termux.view;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.text.InputType;

import org.junit.Test;

public class TerminalViewInputConnectionTest {

    @Test
    public void currentModePreservesTypeNullByDefault() {
        assertEquals(InputType.TYPE_NULL,
            TerminalView.getTerminalSelectedInputType(TerminalViewClient.TERMINAL_INPUT_MODE_CURRENT, false));
    }

    @Test
    public void currentModePreservesVisiblePasswordNoSuggestionsWhenCharInputIsEnforced() {
        assertEquals(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS,
            TerminalView.getTerminalSelectedInputType(TerminalViewClient.TERMINAL_INPUT_MODE_CURRENT, true));
    }

    @Test
    public void directGboardModeUsesTextClassAndAllowsSuggestions() {
        int inputType = TerminalView.getTerminalSelectedInputType(TerminalViewClient.TERMINAL_INPUT_MODE_DIRECT_GBOARD, true);

        assertEquals(InputType.TYPE_CLASS_TEXT, inputType & InputType.TYPE_MASK_CLASS);
        assertFalse((inputType & InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS) != 0);
        assertFalse((inputType & InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) != 0);
        assertTrue((inputType & InputType.TYPE_TEXT_FLAG_MULTI_LINE) != 0);
    }
}
