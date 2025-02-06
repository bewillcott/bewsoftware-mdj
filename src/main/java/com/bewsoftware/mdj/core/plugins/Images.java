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
import static com.bewsoftware.utils.string.Strings.notEmpty;
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
 * @version 0.8.0
 */
public class Images implements TextConvertor
{
    public Images()
    {
    }

    @Override
    public TextEditor execute(final TextEditor text)
    {
        processInlineImages(text);
        text.replaceAll(Image.PATTERN, new Image());

        return text;
    }

    private void processInlineImages(final TextEditor text)
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
        public String process(final Matcher m)
        {
            final String replacementText;
            final String wholeMatch = m.group(1);
            final String altText = m.group(2);
            String id = m.group(3);

            id = processId(id, altText);

            // imageDefinition is the same as linkDefinition
            final LinkDefinition defn = linkDefinitions.get(id);

            if (defn != null)
            {
                replacementText = processLinkDefinition(defn, altText);
            } else
            {
                replacementText = wholeMatch;
            }

            return replacementText;
        }

        private String processId(final String id, final String altText)
        {
            // [id] is now case sensitive
            return id == null || "".equals(id) ? altText : id;
        }

        private String processLinkDefinition(final LinkDefinition defn, final String altText)
        {
            final String replacementText;
            final String url = processUrl(defn);

            final StringBuilder titleTag = processTitle(defn, altText);

            replacementText = "<img src=\"" + url + "\"" + titleTag + ">";

            return replacementText;
        }

        private StringBuilder processTitle(final LinkDefinition defn, final String altText)
        {
            String title = defn.title;
            final StringBuilder titleTag
                    = new StringBuilder(" alt=\"")
                            .append(altText)
                            .append("\"");

            if (notEmpty(title))
            {
                title = protectEmphasis(title);
                titleTag.append(" title=\"").append(title).append("\"");
            }

            return titleTag;
        }

        private String processUrl(final LinkDefinition defn)
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
