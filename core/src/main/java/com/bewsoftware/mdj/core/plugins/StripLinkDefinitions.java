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

import com.bewsoftware.mdj.core.utils.LinkDefinition;
import com.bewsoftware.mdj.core.utils.TextEditor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.bewsoftware.mdj.core.plugins.PluginInterlink.encodeAmpsAndAngles;
import static com.bewsoftware.mdj.core.plugins.TextConvertor.replaceAll;
import static com.bewsoftware.mdj.core.plugins.utils.Constants.CLASS_REGEX_OPT;
import static com.bewsoftware.mdj.core.plugins.utils.Constants.linkDefinitions;
import static java.util.regex.Pattern.MULTILINE;
import static java.util.regex.Pattern.compile;

/**
 * StripLinkDefinitions class description.
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 0.6.13
 * @version 0.6.13
 */
public class StripLinkDefinitions implements TextConvertor
{
    public StripLinkDefinitions()
    {
    }

    @Override
    public TextEditor execute(TextEditor text)
    {
        Pattern p = compile(
                "^[ ]{0,3}\\[(?<id>.+)\\]:"
                // ID = $1
                // BW:
                + CLASS_REGEX_OPT
                + "[ ]*\\n?[ ]*"
                // Space
                + "<?(?<url>[^'\"]+?)?>?"
                // URL = $2
                + "[ ]*\\n?[ ]*"
                // Space
                + "(?:(?<quote>['\"])(?<title>.+?)\\k<quote>[ ]*)?"
                // Optional title = $3
                + "(?:\\n+|\\Z)",
                MULTILINE);

        text.replaceAll(p, (Matcher m) ->
        {
            String id = m.group("id");
            String classes = m.group("classes");
            String url = m.group("url");
            String title = m.group("title");

            if (classes == null)
            {
                classes = "";
            }

            if (url != null)
            {
                url = encodeAmpsAndAngles(new TextEditor(url)).toString();
            }

            if (title == null)
            {
                title = "";
            }

            title = replaceAll(title, "\"", "&quot;");
            linkDefinitions.put(id, new LinkDefinition(classes, url, title));
            return "";
        });

        return text;
    }
}
