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
import java.util.Arrays;
import java.util.Collection;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @since 0.6.7
 * @version 0.8.0
 */
public class PreserveHtmlBlockTagsTest
{
    public static Collection<String[]> testHtml()
    {
        return Arrays.asList(new String[][]
        {
            {
                "<h1>Chapter One</h1>"
            },
            {
                "<H1>Chapter One</H1>"
            },
            {
                "<div>\n  <div>Text</div>\n</div>"
            },
            {
                "<DIV>\n  <DIV>Text</DIV>\n</DIV>"
            },
            {
                "<TABLE>\n<TR>\n<TD>Cell</TD>\n</TR>\n</TABLE>"
            },
            {
                "<BlockQuote>All the world’s a stage…</BlockQuote>"
            },
            {
                "<iFrame src='http://microsoft.com/'></IFRAME>"
            },
            {
                "<hr/>"
            },
            {
                "<HR>"
            },
            {
                "<!-- a comment -->"
            }
        });
    }

    @ParameterizedTest(name = "{index}: {1}")
    @MethodSource("testHtml")
    public void testRoundtripPreservesTags(final String value)
    {
        assertEquals(value, MarkdownProcessor.convert(value).trim());
    }
}
