/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.markdownj.test;

import org.markdownj.MarkdownProcessor;

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
        System.out.println("Shoudl be: " + TEST_TEXT);
        System.out.println("Result is: " + result);
    }
}
