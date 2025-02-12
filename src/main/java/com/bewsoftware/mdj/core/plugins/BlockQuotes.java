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
import static com.bewsoftware.mdj.core.plugins.PluginInterlink.runBlockGamut;
import static com.bewsoftware.mdj.core.plugins.TextConvertor.replaceAll;
import static java.util.regex.Pattern.DOTALL;
import static java.util.regex.Pattern.MULTILINE;
import static java.util.regex.Pattern.compile;

/**
 * Build block-quotes, including cited block-quotes.
 * <p>
 * <b>Changes:</b>
 * <ul>
 * <li>Add in-text citation reference/link:
 * <pre><code>
 *[(&lt;in-text citation&gt;)](&lt;#link&gt;)
 *> Quote block
 *> of text being quoted.
 * </code></pre>
 * </li>
 * <li>The '#' is required: [(Smith, 2021)](#cite01)<br>
 * Therefore you must use it in the corresponding reference at the bottom of
 * the
 * page, in your references section:<br>
 * <pre><code>
 * **References:**
 *
 * [#cite01]
 * Smith, J. (2021). _A Rose in Time_.  Retrieved from
 * https://www.example.com/making-sense-of-a-rose-in-time
 * </code></pre>
 * You will note that this will produce a paragraph with an attribute:
 * {@code id="cite01"}.
 * </li>
 * </ul>
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 0.6.13
 * @version 0.8.0
 */
public class BlockQuotes implements TextConvertor
{
    public BlockQuotes()
    {
    }

    private static String deleteAll(final String text, final String regex)
    {
        return replaceAll(text, regex, "");
    }

    @Override
    public TextEditor execute(final TextEditor text)
    {
        return text.replaceAll(BlockQuote.PATTERN, new BlockQuote());
    }

    private class BlockQuote implements Replacement
    {
        public static final Pattern PATTERN = compile(
                // Citation link: [(Cade, 2015)](#cite01)
                "(?<citetext>\\[\\([A-Z][^\\)]*?\\)\\]\\(#[^\\)]+\\)[ ]*\\n)?"
                + "(?<blockquote>("
                + "^[ \t]*>[ \t]?"
                // > at the start of a line
                + ".+\\n"
                // rest of the first line
                + "(.+\\n)*"
                // subsequent consecutive lines
                + "\\n*"
                // blanks
                + ")+"
                + ")", MULTILINE);

        private BlockQuote()
        {
        }

        @Override
        public String process(final Matcher m)
        {
            String rtn;
            final String citeText = processCitation(m);
            final TextEditor blockQuote = processBlockQuote(m);

            if (citeText.isBlank())
            {
                rtn = "<blockquote>\n" + blockQuote + "\n</blockquote>\n\n";
            } else
            {
                rtn = "<div class=\"blockquote\">\n" + blockQuote + "\n" + citeText + "</div>\n\n";
            }

            return rtn;
        }

        private void cleanupBlockQuotedText(final TextEditor blockQuote)
        {
            blockQuote.deleteAll("^[ \t]*>[ \t]?");
            blockQuote.deleteAll("^[ \t]+$");
        }

        private void indentAllLines(final TextEditor blockQuote)
        {
            blockQuote.replaceAll("^", "  ");
        }

        private TextEditor outdentPreTagBlocks(final TextEditor blockQuote)
        {
            final Pattern p1 = compile("(\\s*<pre>.*?</pre>)", DOTALL);

            final TextEditor rtn = blockQuote.replaceAll(p1, (final Matcher m1) ->
            {
                String pre = m1.group(1);
                return deleteAll(pre, "^  ");
            });

            return rtn;
        }

        private TextEditor processBlockQuote(final Matcher m)
        {
            TextEditor blockQuote = new TextEditor(m.group("blockquote"));
            cleanupBlockQuotedText(blockQuote);
            blockQuote = runBlockGamut(blockQuote);
            indentAllLines(blockQuote);
            blockQuote = outdentPreTagBlocks(blockQuote);

            return blockQuote;
        }

        private String processCitation(final Matcher m)
        {
            //
            // BW: Process citation link: [(<citeText)](<#link>)
            //
            String citeText = m.group("citetext");

            if (citeText != null && !citeText.isBlank())
            {
                TextEditor te = new TextEditor(citeText)
                        .replaceAllLiteral("\\)\\]\\(#", ")][@cite](#");
                citeText = "  " + doAnchors(te).toString();
            } else
            {
                citeText = "";
            }

            return citeText;
        }
    }
}
