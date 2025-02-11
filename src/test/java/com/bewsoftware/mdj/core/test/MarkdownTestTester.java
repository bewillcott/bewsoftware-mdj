/*
 * Copyright (c) 2005, Pete Bevin.
 * <http://markdownj.petebevin.com>
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *  - Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  - Neither the name "Markdown" nor the names of its contributors may
 *    be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * This software is provided by the copyright holders and contributors "as
 * is" and any express or implied warranties, including, but not limited
 * to, the implied warranties of merchantability and fitness for a
 * particular purpose are disclaimed. In no event shall the copyright owner
 * or contributors be liable for any direct, indirect, incidental, special,
 * exemplary, or consequential damages (including, but not limited to,
 * procurement of substitute goods or services; loss of use, data, or
 * profits; or business interruption) however caused and on any theory of
 * liability, whether in contract, strict liability, or tort (including
 * negligence or otherwise) arising in any way out of the use of this
 * software, even if advised of the possibility of such damage.
 *
 */
package com.bewsoftware.mdj.core.test;

import com.bewsoftware.mdj.MarkdownProcessor;
import com.bewsoftware.utils.string.Diff;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static com.bewsoftware.utils.string.Strings.println;
import static org.junit.jupiter.api.Assertions.assertFalse;

/*
 * Modified to use JUnit5 instead of JUnit 4.
 *
 * Bradley Willcott (7/1/2020)
 *
 * @since 0.6.7
 * @version 0.8.0
 */
public class MarkdownTestTester
{
    private final static String MARKDOWN_TEST_DIR = "/MarkdownTest";
//    private final static String MARKDOWN_TEST_DIR = "/MarkdownTest/short";

    public static Collection<String[]> markdownTests()
    {
        final List<String[]> list = new ArrayList<>();
        final URL fileUrl = MarkdownTestTester.class.getResource(MARKDOWN_TEST_DIR);
        File dir;

        try
        {
            dir = new File(fileUrl.toURI());
        } catch (URISyntaxException e)
        {
            dir = new File(fileUrl.getFile());
        }

        final File[] dirEntries = dir.listFiles();

        for (final File dirEntry : dirEntries)
        {
            final String fileName = dirEntry.getName();

            if (fileName.endsWith(".text"))
            {
                final String testName = fileName.substring(0, fileName.lastIndexOf('.'));

                list.add(new String[]
                {
                    MARKDOWN_TEST_DIR, testName
                });
            }
        }

        return list;
    }

    @ParameterizedTest(name = "{index}: {arguments}")
    @MethodSource("markdownTests")
    public void runTest(final String dir, final String test) throws IOException
    {
        println("runTest: %s, %s", dir, test);
        println("Test: %s", test);
        final String testText = slurp(dir + File.separator + test + ".text");
        final String htmlText = slurp(dir + File.separator + test + ".html");

        final String markdownText = MarkdownProcessor.convert(testText);

        final boolean result;

        if (result = diffStrings(htmlText, markdownText))
        {
            println("-----------------------------------------------------------------\n"
                    + "testText:%n%s"
                    + "\n-----------------------------------------------------------------", testText);

            println("-----------------------------------------------------------------\n"
                    + "markdownText:%n%s"
                    + "\n-----------------------------------------------------------------", markdownText);
        }

        assertFalse(result, test);
    }

    /**
     * Check whether or not the strings are different.
     *
     * @param orig The original text.
     * @param mod  The modified text.
     *
     * @return {@code true} if different, {@code false} if same.
     */
    private boolean diffStrings(final String orig, final String mod)
    {
        boolean rtn = true;
        final List<Diff.ModifiedLine> mLines = Diff.lines(orig, mod);

        if (!mLines.isEmpty())
        {
            mLines.forEach(line -> println("----------%n%s", line));
        } else
        {
            rtn = false;
        }

        return rtn;
    }

    /**
     * Read contents of file into a String.
     *
     * @param fileName File to read.
     *
     * @return String containing contents of file.
     *
     * @throws IOException if any.
     */
    private String slurp(final String fileName) throws IOException
    {
        final URL fileUrl = this.getClass().getResource(fileName);
        final File file = new File(URLDecoder.decode(fileUrl.getFile(), "UTF-8"));

        final StringBuilder sb;

        try (BufferedReader in = new BufferedReader(new FileReader(file)))
        {
            sb = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null)
            {
                sb.append(line).append("\n");
            }
        }

        return sb.toString();
    }
}
