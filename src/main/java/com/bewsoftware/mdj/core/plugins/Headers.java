/*
 * Copyright (c) 2005, Martian Software
 * Authors: Pete Bevin, John Mutchek
 * http://www.martiansoftware.com/markdownj
 *
 * Copyright (c) 2020, 2021, 2025 Bradley Willcott
 * https://github.com/bewillcott/bewsoftware-mdj
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.bewsoftware.mdj.core.plugins.PluginInterlink.doAnchors;
import static com.bewsoftware.mdj.core.plugins.utils.Attributes.addId;
import static com.bewsoftware.mdj.core.plugins.utils.Constants.ID_REGEX_OPT;
import static com.bewsoftware.utils.string.Strings.notBlank;
import static java.util.regex.Pattern.MULTILINE;
import static java.util.regex.Pattern.compile;

/**
 * Build headers: &lt;h1&gt; - &lt;h6&gt;.
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 0.6.13
 * @version 0.8.0
 */
public class Headers implements TextConvertor
{
    public Headers()
    {
    }

    @Override
    public TextEditor execute(final TextEditor text)
    {
        setExtStyleHeaders(text);
        return text.replaceAll(Header.PATTERN, new Header());
    }

    private void setExtStyleHeaders(final TextEditor text)
    {
        // set ext-style headers
        text.replaceAll("^(.*)\n[=]{4,}$", "<h1>$1</h1>");
        text.replaceAll("^(.*)\n[-]{4,}$", "<h2>$1</h2>");
    }

    private class Header implements Replacement
    {
        // atx-style headers - e.g., "#### heading 4 ####"
        // Added Anchor option to header text
        // Bradley Willcott (11/03/2020)
        public static final Pattern PATTERN = compile(
                "^(?<marker>[#]{1,6})" + ID_REGEX_OPT
                + "[ ]*(?<heading>(?<brkt1>\\[)?[^\\]]*?(?<brkt2>(\\]|\\]\\[[^\\]]*?\\])!?)?)"
                + "[ ]*(?:\\k<marker>[ ]*(?<tail>.*)?)?$", MULTILINE);

        private Header()
        {
        }

        @Override
        public String process(final Matcher m)
        {
            final String marker = m.group("marker");
            String id = m.group("id");
            String heading = m.group("heading");
            final String brkt1 = m.group("brkt1");
            final String brkt2 = m.group("brkt2");
            String tail = m.group("tail");

            id = processId(id);
            heading = processHeading(brkt1, brkt2, heading);
            tail = processTail(tail);

            return finalizeHeadingTag(marker, id, heading, tail);
        }

        private String finalizeHeadingTag(
                final String marker,
                final String id,
                final String heading,
                final String tail
        )
        {
            final int level = marker.length();
            final String tag = "h" + level;

            return "<" + tag + id + ">" + heading + tail + "</" + tag + ">\n";
        }

        private String processHeading(
                final String brkt1,
                final String brkt2,
                final String heading
        )
        {
            String rtn = heading;

            // Process header text, looking for anchor option (BW)
            if (brkt1 != null && brkt2 != null)
            {
                rtn = doAnchors(new TextEditor(heading)).toString();
            }
            return rtn;
        }

        private String processId(final String id)
        {
            String rtn;

            if (id != null)
            {
                rtn = addId(id);
            } else
            {
                rtn = "";
            }

            return rtn;
        }

        private String processTail(final String tail)
        {
            String rtn;

            if (notBlank(tail))
            {
                rtn = " " + doAnchors(new TextEditor(tail)).toString();
            } else
            {
                rtn = "";
            }

            return rtn;
        }
    }
}
