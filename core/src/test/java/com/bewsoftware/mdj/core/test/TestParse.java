package com.bewsoftware.mdj.core.test;

import com.bewsoftware.mdj.core.MarkdownProcessor;

/**
 *
 * @author Bradley Willcott
 */
public class TestParse {

    private static final String TEST_TEXT = "**Parse** *this* ~~string~~";

    public static void main(String[] args) {
        String result = MarkdownProcessor.markdown(TEST_TEXT);

        System.out.println("org.markdownj.test.TestParse.main()");
        System.out.println("Source: " + TEST_TEXT);
        System.out.println("Result is: " + result);
    }
}
