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

import com.bewsoftware.mdj.core.TextEditor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.bewsoftware.mdj.core.plugins.utils.Attributes.addClass;
import static java.util.regex.Pattern.MULTILINE;
import static java.util.regex.Pattern.compile;

/**
 * Build horizontal rules.
 * <p>
 * <b>Changes:</b>
 * <ul>
 * <li>Three different classes of &lt;hr&gt;
 * <ul>
 * <li>"---" : &lt;hr&gt;</li>
 * <li>"***" : &lt;hr class="bold"&gt;</li>
 * <li>"===" : &lt;hr class="thick"&gt;</li>
 * </ul>
 * </li>
 * <li>Changed `_` to `=`</li>
 * </ul>
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 0.6.13
 * @version 0.6.13
 */
public class HorizontalRules implements TextConvertor
{
    public HorizontalRules()
    {
    }

    @Override
    public TextEditor execute(TextEditor text)
    {
        Pattern p = compile(
                "^[ ]{0,3}(?<type>([*][ ]*){3,}|([-][ ]*){3,}|([=][ ]*){3,})[ ]*$",
                MULTILINE);

        text.replaceAll(p, (Matcher m) ->
        {
            String type = m.group("type");
            String classes = "";

            switch (type.charAt(0))
            {
                case '-':
                    break;

                case '*':
                    classes = addClass("bold");
                    break;

                case '=':
                    classes = addClass("thick");
                    break;

                default:
                    break;

            }

            return "<hr" + classes + ">";
        });

        return text;
    }

}
