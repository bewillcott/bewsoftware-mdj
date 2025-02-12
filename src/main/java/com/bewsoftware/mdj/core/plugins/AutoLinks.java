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
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.bewsoftware.mdj.core.plugins.PluginInterlink.unEscapeSpecialChars;
import static com.bewsoftware.mdj.core.plugins.utils.Attributes.addClass;
import static com.bewsoftware.mdj.core.plugins.utils.Constants.CLASS_REGEX_OPT;
import static com.bewsoftware.mdj.core.plugins.utils.Constants.TARGET;
import static java.util.regex.Pattern.compile;

/**
 * AutoLinks class description.
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 0.6.13
 * @version 0.8.0
 */
public class AutoLinks implements TextConvertor
{
    public AutoLinks()
    {
    }

    private static void appendDecimalValue(final StringBuilder sb, final char ch)
    {
        sb.append("&#");
        sb.append((int) ch);
        sb.append(';');
    }

    private static void appendHexValue(final StringBuilder sb, final char ch)
    {
        sb.append("&#x");
        sb.append(Integer.toString((int) ch, 16));
        sb.append(';');
    }

    private static String encodeEmail(final String s)
    {
        final StringBuilder sb = new StringBuilder();
        char[] email = s.toCharArray();

        for (char ch : email)
        {
            processCharInEmail(sb, ch);
        }

        return sb.toString();
    }

    private static void processCharInEmail(final StringBuilder sb, final char ch)
    {
        double r = new Random().nextDouble();

        if (r < 0.45)
        {
            appendDecimalValue(sb, ch);
        } else if (r < 0.9)
        {
            appendHexValue(sb, ch);
        } else
        {
            sb.append(ch);
        }
    }

    @Override
    public TextEditor execute(final TextEditor text)
    {
        processLinks(text);
        processEmails(text);
        return text;
    }

    private void processEmails(final TextEditor text)
    {
        text.replaceAll(Email.PATTERN, new Email());
    }

    private void processLinks(final TextEditor text)
    {
        text.replaceAll(Link.PATTERN, new Link());
    }

    private class Email implements Replacement
    {
        public static final Pattern PATTERN = compile(
                "<([-.\\w]+\\@[-a-z0-9]+(?:\\.[-a-z0-9]+)*\\.[a-z]+)>");

        @Override
        public String process(final Matcher m)
        {
            final String address = m.group(1);
            final TextEditor ed = new TextEditor(address);
            unEscapeSpecialChars(ed);
            final String addr = encodeEmail(ed.toString());
            final String url = encodeEmail("mailto:" + ed.toString());
            return "<a href=\"" + url + "\">" + addr + "</a>";
        }
    }

    private class Link implements Replacement
    {
        public static final Pattern PATTERN = Pattern.compile(
                "<"
                + "(?<url>(?:https?|ftp):[^'\">\\s]+)"
                + ">"
                + "(?<target>!)?"
                + CLASS_REGEX_OPT);

        private Link()
        {
        }

        @Override
        public String process(final Matcher m)
        {
            final String url = m.group("url");
            final String targetTag = m.group("target") != null ? TARGET : "";
            final String classes = m.group("classes");
            final String classAtrib = classes != null && !classes.isBlank()
                    ? addClass(classes) : "";

            return "<a href=\"" + url + "\""
                    + classAtrib + targetTag + ">" + url + "</a>";
        }
    }
}
