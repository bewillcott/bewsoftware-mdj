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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @since 0.6.7
 * @version 0.8.0
 */
public class MarkupFileTester
{
    private final static String[] TEST_FILENAMES = new String[]
    {
        "/dingus.txt",
        "/paragraphs.txt",
        "/snippets.txt",
        "/lists.txt"
    };

    public static Collection<TestResultPair[]> testResultPairs() throws IOException
    {
        final List<TestResultPair> fullResultPairList = new ArrayList<>();

        for (String filename : TEST_FILENAMES)
        {
            fullResultPairList.addAll(newTestResultPairList(filename));
        }

        final ArrayList<TestResultPair[]> testResultPairs = new ArrayList<>();

        fullResultPairList.forEach((p) ->
        {
            testResultPairs.add(new TestResultPair[]
            {
                p
            });
        });

        return testResultPairs;
    }

    private static void addTestResultPair(
            final List<TestResultPair> list,
            final StringBuilder testbuf,
            final StringBuilder resultbuf,
            final String testNumber,
            final String testName
    )
    {
        if (testbuf == null || resultbuf == null)
        {
            return;
        }

//        String test = chomp(testbuf.toString());
//        String result = chomp(resultbuf.toString());
        final String test = testbuf.toString().stripTrailing();
        final String result = resultbuf.toString().stripTrailing();
        final String id = testNumber + "(" + testName + ")";

        list.add(new TestResultPair(id, test, result));
    }

    private static List<TestResultPair> newTestResultPairList(final String filename) throws IOException
    {
        final Pattern pTest = Pattern.compile("# Test (\\w+) \\((.*)\\)");
        final Pattern pResult = Pattern.compile("# Result (\\w+)");

        final List<TestResultPair> list = new ArrayList<>();
        final URL fileUrl = MarkupFileTester.class.getResource(filename);
        File file;

        try
        {
            file = new File(fileUrl.toURI());
        } catch (URISyntaxException e)
        {
            file = new File(fileUrl.getFile());
        }

        try (final BufferedReader in = new BufferedReader(new FileReader(file)))
        {
            StringBuilder test = new StringBuilder();
            StringBuilder result = new StringBuilder();
            StringBuilder curbuf = new StringBuilder();

            String line;
            int lineNumber = 0;

            String testNumber = null;
            String testName = null;

            while ((line = in.readLine()) != null)
            {
                lineNumber++;
                final Matcher mTest = pTest.matcher(line);
                final Matcher mResult = pResult.matcher(line);

                if (mTest.matches())
                { // # Test
                    addTestResultPair(list, test, result, testNumber, testName);
                    testNumber = mTest.group(1);
                    testName = mTest.group(2);
                    test = new StringBuilder();
                    result = new StringBuilder();
                    curbuf = test;
                } else if (mResult.matches())
                { // # Result
                    if (testNumber == null)
                    {
                        throw new RuntimeException("Test file has result without a test (line " + lineNumber + ")");
                    }

                    final String resultNumber = mResult.group(1);

                    if (!testNumber.equals(resultNumber))
                    {
                        throw new RuntimeException("Result " + resultNumber + " test " + testNumber + " (line " + lineNumber + ")");
                    }

                    curbuf = result;
                } else
                {
                    curbuf.append(line).append("\n");
                }
            }

            addTestResultPair(list, test, result, testNumber, testName);
        }

        return list;
    }

//    private static String chomp(String s) {
//        int lastPos = s.length() - 1;
//
//        while (s.charAt(lastPos) == '\n' || s.charAt(lastPos) == '\r') {
//            lastPos--;
//        }
//
//        return s.substring(0, lastPos + 1);
//    }
    @ParameterizedTest(name = "{index}: {1}")
    @MethodSource("testResultPairs")
    public void runTest(final TestResultPair pair)
    {
        assertEquals(pair.result.trim(), MarkdownProcessor.convert(pair.test).trim(), pair.name);
    }
}
