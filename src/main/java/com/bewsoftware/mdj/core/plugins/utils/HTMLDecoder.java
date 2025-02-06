/*
 * Copyright (c) 2005, Pete Bevin.
 * <http://markdownj.petebevin.com>
 *
 * Copyright (c) 2021 Bradley Willcott
 * Refactored.
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
package com.bewsoftware.mdj.core.plugins.utils;

import com.bewsoftware.mdj.core.TextEditor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is a utility class. It provides a number of helper methods.
 *
 * @author Bradley Willcott
 *
 * @since 0.6.3
 * @version 0.8.0
 */
public interface HTMLDecoder
{
    public static String decode(final String html)
    {
        final TextEditor ed = new TextEditor(html);
        replaceAllDecimalChars(ed);
        replaceAllHexadecimalChars(ed);

        return ed.toString();
    }

    private static void replaceAllDecimalChars(final TextEditor ed)
    {
        final Pattern p = Pattern.compile("&#(\\d+);");

        ed.replaceAll(p, (Matcher m) ->
        {
            final String charDecimal = m.group(1);
            final char ch = (char) Integer.parseInt(charDecimal);

            return Character.toString(ch);
        });
    }

    private static void replaceAllHexadecimalChars(final TextEditor ed)
    {
        final Pattern p = Pattern.compile("&#x([0-9a-fA-F]+);");

        ed.replaceAll(p, (Matcher m) ->
        {
            final String charHex = m.group(1);
            final char ch = (char) Integer.parseInt(charHex, 16);

            return Character.toString(ch);
        });
    }
}
