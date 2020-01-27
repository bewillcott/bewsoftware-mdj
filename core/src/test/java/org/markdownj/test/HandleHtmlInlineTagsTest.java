package org.markdownj.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.markdownj.MarkdownProcessor;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HandleHtmlInlineTagsTest {

    private MarkdownProcessor m;

    @BeforeEach
    public void createProcessor() {
        m = new MarkdownProcessor();
    }

    @Test
    public void testInlineTagsWithMarkdown() {
        assertEquals("<p><span><strong>Something really enjoyable!</strong></span></p>",
                     m.markdown("<span>**Something really enjoyable!**</span>").trim());
    }

    @Test
    public void testUpperCaseInlineTagsWithMarkdown() {
        assertEquals("<p><SPAN><strong>oh no</strong></SPAN></p>",
                     m.markdown("<SPAN>**oh no**</SPAN>").trim());
    }

}
