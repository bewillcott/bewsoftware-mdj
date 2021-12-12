/*
 * Copyright (c) 2020, 2021 Bradley Willcott.
 * <http://www.bewsoftware.com>
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
 */
package com.bewsoftware.mdj.core;

/**
 * This is a utility class. It provides a number of helper methods.
 *
 * @author Bradley Willcott
 *
 * @since 0.6.3
 * @version 0.6.7
 */
public final class Attributes
{

    /**
     * Wraps the texts into a "class=" string.
     *
     * @param texts A series of classes to include.
     *
     * @return new attribute text.
     */
    public static String addClass(String... texts)
    {
        String rtn = "";

        if (texts != null && texts.length > 0)
        {
            rtn = processTheTexts(texts);
        }

        return rtn;
    }

    /**
     * Wraps the text into a "id=" string.
     *
     * @param text to be wrapped.
     *
     * @return new attribute text.
     */
    public static String addId(String text)
    {
        return processText(" id=\"%s\"", text);
    }

    /**
     * Wraps the text into a "style=" string.
     *
     * @param text to be wrapped.
     *
     * @return new attribute text.
     */
    public static String addStyle(String text)
    {
        return processText(" style=\"%s\"", text);
    }

    private static String processTheTexts(String[] texts)
    {
        String stringOfClasses = gatherClasses(texts);
        return processText(" class=\"%s\"", stringOfClasses);
    }

    private static String gatherClasses(String[] texts)
    {
        StringBuilder classes = new StringBuilder();

        for (String text : texts)
        {
            if (text != null && !text.isBlank())
            {
                classes.append(text).append(" ");
            }
        }

        return classes.toString().trim();
    }

    private static String processText(String format, String text)
    {
        String rtn = "";

        if (text != null && !text.isBlank())
        {
            rtn = String.format(format, text);
        }

        return rtn;
    }

    /**
     * Not meant to be instantiated.
     */
    private Attributes()
    {
    }
}
