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

import com.bewsoftware.mdj.core.HTMLToken;
import com.bewsoftware.mdj.core.TextEditor;
import java.util.Collection;

import static com.bewsoftware.mdj.core.MarkdownProcessor.CHAR_PROTECTOR;

/**
 * Escape special characters within tags.
 * <ul>
 * <li>Meaning between '{@literal <} ' and '{@literal >} '</li>
 * <li>Encode [\`*_] so they don't conflict with their use in Markdown for code,
 * italics and strong.</li>
 * We're replacing each such character with its corresponding random string
 * value; this is likely overkill, but it should prevent us from colliding with
 * the escape values by accident.
 * </ul>
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 1.0
 * @version 1.0
 */
public class EscapeSpecialCharsWithinTagAttributes implements TextConvertor
{
    public EscapeSpecialCharsWithinTagAttributes()
    {
    }

    @Override
    public TextEditor execute(TextEditor text)
    {
        Collection<HTMLToken> tokens = text.tokenizeHTML();
        TextEditor newText = new TextEditor("");

        tokens.stream().map(token ->
        {
            String value = token.text;

            if (token.isTag)
            {
                value = value.replaceAll("\\\\", CHAR_PROTECTOR.encode("\\"));
                value = value.replaceAll("`", CHAR_PROTECTOR.encode("`"));
                value = value.replaceAll("\\*", CHAR_PROTECTOR.encode("*"));
                value = value.replaceAll("_", CHAR_PROTECTOR.encode("_"));
            }

            return value;
        }).forEachOrdered(value -> newText.append(value));

        return newText;
    }

}
