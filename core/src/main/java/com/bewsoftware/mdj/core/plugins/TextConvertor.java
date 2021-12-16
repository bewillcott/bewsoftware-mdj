/*
 * Copyright (c) 2005, Martian Software
 * Authors: Pete Bevin, John Mutchek
 * http://www.martiansoftware.com/markdownj
 *
 * Copyright (c) 2020, 2021 Bradley Willcott
 * Modifications to the code.
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
 */
package com.bewsoftware.mdj.core.plugins;

import com.bewsoftware.mdj.core.Replacement;
import com.bewsoftware.mdj.core.TextEditor;
import java.util.regex.Pattern;

import static com.bewsoftware.mdj.core.plugins.utils.Constants.CHAR_PROTECTOR;

/**
 * TextConvertor interface description.
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 0.6.13
 * @version 0.6.13
 */
public interface TextConvertor
{
    public static boolean hasParagraphBreak(final TextEditor text)
    {
        return text.toString().contains("\n\n");
    }

    public static boolean isEmptyString(final String text)
    {
        return text == null || text.isEmpty();
    }

    public static String protectEmphasis(String subtext)
    {
        // protect emphasis (* and _) within urls
        subtext = subtext.replaceAll("\\*", CHAR_PROTECTOR.encode("*"));
        subtext = subtext.replaceAll("_", CHAR_PROTECTOR.encode("_"));
        return subtext;
    }

    public static String replaceAll(final String text, final String regex, final String replacement)
    {
        return new TextEditor(text)
                .replaceAll(regex, replacement)
                .toString();
    }

    public static String replaceAll(final String text, final Pattern pattern, final Replacement replacement)
    {
        return new TextEditor(text)
                .replaceAll(pattern, replacement)
                .toString();
    }

    public TextEditor execute(TextEditor text);

}
