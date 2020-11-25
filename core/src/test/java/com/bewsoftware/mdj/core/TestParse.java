package com.bewsoftware.mdj.core;

import com.bewsoftware.mdj.core.MarkdownProcessor;

/**
 *
 * @author Bradley Willcott
 */
public class TestParse {

    private static final String TEST_TEXT = "**Parse** *this* ~~string~~";

    public static void main(String[] args) {
        MarkdownProcessor mp = new MarkdownProcessor();

        String result = mp.markdown(TEST_TEXT);

        System.out.println("org.markdownj.test.TestParse.main()");
        System.out.println("Source: " + TEST_TEXT);
        System.out.println("Result is: " + result);
    }
}
