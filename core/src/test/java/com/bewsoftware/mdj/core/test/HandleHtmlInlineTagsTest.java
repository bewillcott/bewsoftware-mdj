package com.bewsoftware.mdj.core.test;

import com.bewsoftware.mdj.core.MarkdownProcessor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HandleHtmlInlineTagsTest {

    @Test
    public void testInlineTagsWithMarkdown() {
        assertEquals("<p><span><strong>Something really enjoyable!</strong></span></p>",
                     MarkdownProcessor.markdown("<span>**Something really enjoyable!**</span>").trim());
    }

    @Test
    public void testUpperCaseInlineTagsWithMarkdown() {
        assertEquals("<p><SPAN><strong>oh no</strong></SPAN></p>",
                     MarkdownProcessor.markdown("<SPAN>**oh no**</SPAN>").trim());
    }

}
