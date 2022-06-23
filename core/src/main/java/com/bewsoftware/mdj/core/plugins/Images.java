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
import com.bewsoftware.mdj.core.utils.LinkDefinition;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.bewsoftware.mdj.core.plugins.TextConvertor.protectEmphasis;
import static com.bewsoftware.mdj.core.plugins.utils.Constants.linkDefinitions;
import static java.util.regex.Pattern.compile;

/**
 * Process all image references.
 * <p>
 * <b>Syntax:</b>
 * <ul>
 * <li>Inline image:
 * <ul>
 * <li>{@code ![<alt>](<src>) "<title>"}</li>
 * <li>{@code ![<alt>](<src>)}</li>
 * </ul></li>
 * <li>Reference-style image:
 * <ul>
 * <li>{@code ![<alt>][<id>]}</li>
 * </ul></li>
 * </ul>
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 0.6.13
 * @version 0.6.13
 */
public class Images implements TextConvertor
{
    public Images()
    {
    }

    @Override
    public TextEditor execute(TextEditor text)
    {
        processInlineImages(text);
        text.replaceAll(Image.PATTERN, new Image());
        return text;
    }

    private void processInlineImages(TextEditor text)
    {
        // Inline image syntax
        //
        // Added "(?!@)" to prevent picking up [link]![@class](...)
        //
        // Bradley Willcott (23/12/2020)
        text.replaceAll("!\\[(?!@)([^\\]]*)\\]\\(([^\"]*) \"([^\"]*)\"\\)", "<img src=\"$2\" alt=\"$1\" title=\"$3\">");
        text.replaceAll("!\\[(?!@)([^\\]]*)\\]\\(([^\\)]*)\\)", "<img src=\"$2\" alt=\"$1\">");
    }

    private class Image implements Replacement
    {
        // Reference-style image syntax
        //
        // Added "(?!@)" to prevent picking up [link]![@class](...)
        //
        // Bradley Willcott (23/12/2020)
        public static final Pattern PATTERN = compile("("
                + "!\\[(?!@)([^\\]]*)\\]"
                // alt text = $2
                + "(?:\\[([^\\]]*)\\])?"
                // ID = $3
                + ")");

        private Image()
        {
        }

        @Override
        public String process(Matcher m)
        {
            String replacementText;
            String wholeMatch = m.group(1);
            String altText = m.group(2);
            String id = m.group(3);

            id = processId(id, altText);

            // imageDefinition is the same as linkDefinition
            LinkDefinition defn = linkDefinitions.get(id);

            if (defn != null)
            {
                replacementText = processLinkDefinition(defn, altText);
            } else
            {
                replacementText = wholeMatch;
            }

            return replacementText;
        }

        private String processId(String id, String altText)
        {
            // [id] is now case sensitive
            if (id == null || "".equals(id))
            {
                id = altText;
            }

            return id;
        }

        private String processLinkDefinition(LinkDefinition defn, String altText)
        {
            String replacementText;
            String url = processUrl(defn);

            StringBuilder titleTag = processTitle(defn, altText);

            replacementText = "<img src=\"" + url + "\"" + titleTag + ">";
            return replacementText;
        }

        private StringBuilder processTitle(LinkDefinition defn, String altText)
        {
            String title = defn.title;
            StringBuilder titleTag = new StringBuilder(" alt=\"").append(altText)
                    .append("\"");

            if (title != null && !title.equals(""))
            {
                title = protectEmphasis(title);
                titleTag.append(" title=\"").append(title).append("\"");
            }

            return titleTag;
        }

        private String processUrl(LinkDefinition defn)
        {
            String url = defn.url;

            if (url != null)
            {
                url = protectEmphasis(url);
            }
            return url;
        }

    }
}
